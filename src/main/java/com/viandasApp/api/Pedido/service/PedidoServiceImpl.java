package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.service.NotificacionServiceImpl;
import com.viandasApp.api.Pedido.dto.*;
import com.viandasApp.api.Pedido.mappers.PedidoMapper;
import com.viandasApp.api.Pedido.model.DetallePedido;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.ServiceGenerales.email.EmailService;
import com.viandasApp.api.Pedido.specification.PedidoSpecifications;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.service.ViandaServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioServiceImpl usuarioService;
    private final EmprendimientoServiceImpl emprendimientoService;
    private final ViandaServiceImpl viandaService;
    private final PedidoMapper pedidoMapper;
    private final NotificacionServiceImpl notificacionService;
    private final EmailService emailService;

    //--------------------------Create--------------------------//
    @Transactional
    @Override
    public PedidoDTO createPedido(PedidoCreateDTO pedidoCreateDTO, Usuario usuarioLogueado) {

        if (usuarioLogueado.getRolUsuario().equals(RolUsuario.CLIENTE)
            && !usuarioLogueado.getId().equals(pedidoCreateDTO.getClienteId()))
        {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Sólo podés crear pedidos a tu propio nombre."
            );
        }

        final Long clienteId = pedidoCreateDTO.getClienteId();

        Usuario cliente = usuarioService.findEntityById(clienteId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + clienteId + "."
                        )
                );

        if (!cliente.getRolUsuario().equals(RolUsuario.CLIENTE)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El comprador con ID #" + clienteId + " no es un cliente."
            );
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaEntrega = pedidoCreateDTO.getFechaEntrega();

        if (fechaEntrega == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha de entrega es obligatoria."
            );
        }

        if (fechaEntrega.isBefore(hoy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha de entrega no puede ser en el pasado."
            );
        }

        if (fechaEntrega.isBefore(hoy.plusDays(2))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido requiere al menos 48hs de anticipación. La fecha más próxima posible es: " +
                            hoy.plusDays(2)
            );
        }

        final Long emprendimientoId = pedidoCreateDTO.getEmprendimientoId();

        Emprendimiento emprendimientoPedido = emprendimientoService.findEntityById(emprendimientoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un emprendimiento con ID #" + emprendimientoId + "."
                        )
                );

        Pedido pedido = pedidoMapper.DTOToEntity(pedidoCreateDTO, cliente, emprendimientoPedido);

        cargarItemsAlPedido(pedido, pedidoCreateDTO.getViandas());

        Pedido guardado = pedidoRepository.save(pedido);

        // Notificación interna al dueño
        Long idDuenoEmprendimiento = guardado.getEmprendimiento().getUsuario().getId();
        notificarCambio(
                guardado,
                "Te hicieron un nuevo pedido para tu emprendimiento '" + guardado.getEmprendimiento().getNombreEmprendimiento() +
                        "' para la fecha '" + guardado.getFechaEntrega() +
                        "', se registró con el ID #" + guardado.getId(),
                idDuenoEmprendimiento
        );

        // Envío mail al cliente (Confirmación de pedido)
        if (cliente.getEmail() != null) {
            emailService.sendPedidoConfirmacionCliente(
                    cliente.getEmail(),
                    cliente.getNombreCompleto(),
                    guardado.getId(),
                    emprendimientoPedido.getNombreEmprendimiento(),
                    guardado.getTotal(),
                    guardado.getViandas()
            );
        }

        // Envío mail al dueño (Nuevo pedido)
        Usuario dueno = emprendimientoPedido.getUsuario();
        if (dueno.getEmail() != null) {
            emailService.sendPedidoNuevoDueno(
                    dueno.getEmail(),
                    dueno.getNombreCompleto(),
                    guardado.getId(),
                    cliente.getNombreCompleto(),
                    guardado.getTotal(),
                    guardado.getViandas()
            );
        }

        return new PedidoDTO(guardado);
    }

    //--------------------------Read--------------------------//

    @Override
    public Page<PedidoDTO> buscarPedidos(Usuario usuario, EstadoPedido estado, String nombreEmprendimiento, LocalDate desde, LocalDate hasta, Pageable pageable) {

        Specification<Pedido> spec = Specification.where(null);

        if (usuario.getRolUsuario() == RolUsuario.CLIENTE) {
            spec = spec.and(PedidoSpecifications.delCliente(usuario.getId()));
        } else if (usuario.getRolUsuario() == RolUsuario.DUENO) {
            spec = spec.and(PedidoSpecifications.delDueno(usuario.getId()));
        }

        if (estado != null) {
            spec = spec.and(PedidoSpecifications.hasEstado(estado));
        }
        if (nombreEmprendimiento != null) {
            spec = spec.and(PedidoSpecifications.hasNombreEmprendimiento(nombreEmprendimiento));
        }
        spec = spec.and(PedidoSpecifications.fechaEntregaEntre(desde, hasta));

        if (pageable.getSort().isUnsorted()) {
            spec = spec.and(PedidoSpecifications.conOrdenamientoDefecto());
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return pedidoRepository.findAll(spec, pageable).map(PedidoDTO::new);
    }

    @Override
    public Page<Pedido> buscarEntidadesPedidos(Usuario usuario, EstadoPedido estado, String nombreEmprendimiento, LocalDate desde, LocalDate hasta, Pageable pageable) {

        Specification<Pedido> spec = Specification.where(null);

        if (usuario.getRolUsuario() == RolUsuario.CLIENTE) {
            spec = spec.and(PedidoSpecifications.delCliente(usuario.getId()));
        } else if (usuario.getRolUsuario() == RolUsuario.DUENO) {
            spec = spec.and(PedidoSpecifications.delDueno(usuario.getId()));
        }

        if (estado != null) {
            spec = spec.and(PedidoSpecifications.hasEstado(estado));
        }
        if (nombreEmprendimiento != null) {
            spec = spec.and(PedidoSpecifications.hasNombreEmprendimiento(nombreEmprendimiento));
        }
        spec = spec.and(PedidoSpecifications.fechaEntregaEntre(desde, hasta));

        if (pageable.getSort().isUnsorted()) {
            spec = spec.and(PedidoSpecifications.conOrdenamientoDefecto());
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return pedidoRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un pedido con ID #" + id + "."
                        )
                );

        if (usuario.getRolUsuario().equals(RolUsuario.CLIENTE)) {
            if (!pedido.getUsuario().getId().equals(usuario.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No tenés permiso para ver este pedido."
                );
            }
        }
        else if (usuario.getRolUsuario().equals(RolUsuario.DUENO)) {
            if (!pedido.getEmprendimiento().getUsuario().getId().equals(usuario.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No tenés permiso para ver este pedido."
                );
            }
        }
        return Optional.of(new PedidoDTO(pedido));
    }

    @Override
    public List<String> getNombresEmprendimientosFiltro(Usuario usuario) {
        if (usuario.getRolUsuario() == RolUsuario.ADMIN) {
            return pedidoRepository.findDistinctEmprendimientosAdmin();
        } else if (usuario.getRolUsuario() == RolUsuario.DUENO) {
            return pedidoRepository.findDistinctEmprendimientosDueno(usuario.getId());
        } else {
            return pedidoRepository.findDistinctEmprendimientosCliente(usuario.getId());
        }
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<PedidoDTO> updatePedidoAdmin(Long id, UpdatePedidoDTO updatePedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un pedido con ID #" + id + "."
                        )
                );

        boolean cambioEstado = false, cambioFecha = false;

        if (updatePedidoDTO.getEstado() != null) {
            pedido.setEstado(updatePedidoDTO.getEstado());
            cambioEstado = true;
        }
        if (updatePedidoDTO.getFechaEntrega() != null) {
            pedido.setFechaEntrega(updatePedidoDTO.getFechaEntrega());
            cambioFecha = true;
        }

        Pedido actualizado = pedidoRepository.save(pedido);

        String mensaje = "Tu pedido #" + actualizado.getId();
        if (cambioEstado && cambioFecha) {
            mensaje += " cambió de estado a '" + actualizado.getEstado() + "' y la fecha de entrega a '" + actualizado.getFechaEntrega() + "'";
        } else if (cambioEstado) {
            mensaje += " cambió de estado a '" + actualizado.getEstado() + "'";
        } else if (cambioFecha) {
            mensaje += " cambió la fecha de entrega a '" + actualizado.getFechaEntrega() + "'";
        } else {
            mensaje += " fue actualizado";
        }

        notificarCambio(actualizado, mensaje, actualizado.getUsuario().getId());

        return Optional.of(new PedidoDTO(actualizado));
    }

    @Transactional
    @Override
    public Optional<PedidoDTO> updatePedidoDueno(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "No se encontró un pedido con ID #" + id + "."
                        )
                );

        if (!pedido.getEmprendimiento().getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El emprendimiento del pedido no pertenece al usuario logueado."
            );
        }

        boolean cambioEstado = false;

        if (updatePedidoDTO.getEstado() != null) {
            EstadoPedido estadoActual = pedido.getEstado();
            EstadoPedido nuevoEstado = updatePedidoDTO.getEstado();

            if (estadoActual == EstadoPedido.PENDIENTE) {
                if (nuevoEstado == EstadoPedido.ACEPTADO || nuevoEstado == EstadoPedido.RECHAZADO) {
                    pedido.setEstado(nuevoEstado);
                    cambioEstado = true;
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Un pedido pendiente solo puede ser aceptado o rechazado."
                    );
                }
            }
            else if (estadoActual == EstadoPedido.ACEPTADO) {
                if (nuevoEstado == EstadoPedido.ENTREGADO) {
                    pedido.setEstado(nuevoEstado);
                    cambioEstado = true;
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Un pedido aceptado solo puede pasar a entregado."
                    );
                }
            }
            else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No se puede modificar el estado de un pedido que ya está " + estadoActual + "."
                );
            }
        }

        Pedido actualizado = pedidoRepository.save(pedido);

        String mensaje = "Tu pedido #" + actualizado.getId()
                + ", del emprendimiento '" + pedido.getEmprendimiento().getNombreEmprendimiento() + "'";

        if (cambioEstado) {
            mensaje += " cambió de estado a '" + actualizado.getEstado() + "'";
        } else {
            mensaje += " fue actualizado";
        }

        // Notificación interna
        notificarCambio(actualizado, mensaje, actualizado.getUsuario().getId());

        // Envío mail al cliente si cambió el estado de un pedido
        if (cambioEstado && updatePedidoDTO.getEstado() != null) {
            EstadoPedido nuevo = updatePedidoDTO.getEstado();

            // Solo mando mail si se acepta o rechaza
            if (nuevo == EstadoPedido.ACEPTADO || nuevo == EstadoPedido.RECHAZADO) {
                if (actualizado.getUsuario().getEmail() != null) {
                    emailService.sendPedidoEstadoUpdate(
                            actualizado.getUsuario().getEmail(),
                            actualizado.getUsuario().getNombreCompleto(),
                            actualizado.getId(),
                            actualizado.getEmprendimiento().getNombreEmprendimiento(),
                            nuevo,
                            actualizado.getViandas()
                    );
                }
            }
        }

        return Optional.of(new PedidoDTO(actualizado));
    }


    @Transactional
    @Override
    public Optional<PedidoDTO> updatePedidoCliente(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "No se encontró un pedido con ID #" + id + "."
                        )
                );

        // Validar pertenencia
        if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El pedido que se quiere actualizar no pertenece al usuario logueado."
            );
        }

        // Validar Estado PENDIENTE
        if (!pedido.getEstado().equals(EstadoPedido.PENDIENTE)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Sólo se puede modificar un pedido pendiente."
            );
        }

        boolean cambioFecha = false;
        boolean cambioEstado = false;
        LocalDate hoy = LocalDate.now();

        // Logica para cambiar la fecha
        if (updatePedidoDTO.getFechaEntrega() != null) {

            // REGLA: No se puede cambiar si la entrega es mañana
            if (pedido.getFechaEntrega().equals(hoy.plusDays(1))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede cambiar la fecha porque la entrega es mañana. Contactá al emprendimiento."
                );
            }

            // REGLA: La nueva fecha no puede ser anterior a HOY
            if (updatePedidoDTO.getFechaEntrega().isBefore(hoy)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La nueva fecha no puede ser en el pasado."
                );
            }

            // REGLA SOLICITADA: La nueva fecha no puede ser anterior a la fecha ACTUAL del pedido.
            if (updatePedidoDTO.getFechaEntrega().isBefore(hoy.plusDays(2))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El pedido requiere al menos 48hs de anticipación. La fecha más próxima posible es: " +
                        hoy.plusDays(2)
                );
            }

            pedido.setFechaEntrega(updatePedidoDTO.getFechaEntrega());
            cambioFecha = true;
        }

        // Logica de cancelacion
        if (updatePedidoDTO.getEstado() != null) {

            // El cliente solo puede cancelar
            if (updatePedidoDTO.getEstado() != EstadoPedido.CANCELADO) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El cliente sólo puede cancelar el pedido."
                );
            }

            pedido.setEstado(EstadoPedido.CANCELADO);
            cambioEstado = true;
        }

        Pedido actualizado = pedidoRepository.save(pedido);

        // Construcción del mensaje para notificación
        String mensaje = "El pedido #" + actualizado.getId();
        if (cambioEstado) {
            mensaje += " fue cancelado por el cliente.";
        } else if (cambioFecha) {
            mensaje += " pospuso su entrega para el '" + actualizado.getFechaEntrega() + "'.";
        } else {
            mensaje += " fue actualizado.";
        }

        notificarCambio(actualizado, mensaje, actualizado.getEmprendimiento().getUsuario().getId());

        return Optional.of(new PedidoDTO(actualizado));
    }

    @Transactional
    @Override
    public Optional<PedidoDTO> updateViandasPedidoAdmin(Long pedidoId, PedidoUpdateViandasDTO dto) {
        return pedidoRepository.findById(pedidoId).flatMap(pedido -> {

            pedido.getViandas().clear();
            Emprendimiento emprendimientoPedido = pedido.getEmprendimiento();

            Double total = 0.0;
            for (PedidoUpdateViandasDTO.ViandaCantidadDTO vc : dto.getViandas()) {

                Vianda vianda = viandaService.findEntityViandaById(vc.getViandaId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "No se encontró una vianda con ID #" + vc.getViandaId() + "."
                                )
                        );

                if (!vianda.getEmprendimiento().equals(emprendimientoPedido)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Las viandas deben pertenecer al mismo emprendimiento del pedido."
                    );
                }

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setVianda(vianda);
                detalle.setCantidad(vc.getCantidad());
                detalle.setPrecioUnitario(vianda.getPrecio());
                detalle.setSubtotal(vianda.getPrecio() * vc.getCantidad());
                total += detalle.getSubtotal();
                pedido.agregarDetalle(detalle);
            }
            pedido.setTotal(total);

            Pedido actualizado = pedidoRepository.save(pedido);
            return Optional.of(new PedidoDTO(actualizado));
        });
    }

    @Transactional
    @Override
    public Optional<PedidoDTO> updateViandasPedidoCliente(Long pedidoId, PedidoUpdateViandasDTO dto, Usuario usuarioLogueado) {
        return pedidoRepository.findById(pedidoId).flatMap(pedido -> {

            if (!pedido.getEstado().equals(EstadoPedido.PENDIENTE)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Sólo se pueden actualizar pedidos que se encuentran pendientes."
                );
            }

            if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "El pedido que se quiere actualizar no pertenece al usuario logueado."
                );
            }

            pedido.getViandas().clear();
            Emprendimiento emprendimientoPedido = pedido.getEmprendimiento();

            Double total = 0.0;
            for (PedidoUpdateViandasDTO.ViandaCantidadDTO vc : dto.getViandas()) {

                Vianda vianda = viandaService.findEntityViandaById(vc.getViandaId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "No se encontró una vianda con ID #" + vc.getViandaId() + "."
                                )
                        );

                if (!vianda.getEmprendimiento().equals(emprendimientoPedido)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Las viandas deben pertenecer al mismo emprendimiento del pedido."
                    );
                }

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setVianda(vianda);
                detalle.setCantidad(vc.getCantidad());
                detalle.setPrecioUnitario(vianda.getPrecio());
                detalle.setSubtotal(vianda.getPrecio() * vc.getCantidad());
                total += detalle.getSubtotal();
                pedido.agregarDetalle(detalle);
            }
            pedido.setTotal(total);

            Pedido actualizado = pedidoRepository.save(pedido);
            return Optional.of(new PedidoDTO(actualizado));
        });
    }

    //--------------------------Delete--------------------------//
    @Transactional
    @Override
    public boolean deletePedidoAdmin(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un pedido con ID #" + id + "."
            );
        }
        pedidoRepository.deleteById(id);
        return true;
    }

    @Transactional
    @Override
    public boolean deletePedidoCliente(Long id, Usuario usuarioLogueado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un pedido con ID #" + id + "."
                        )
                );

        if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El pedido que se quiere eliminar no pertenece al usuario logueado."
            );
        }
        if (!(pedido.getEstado().equals(EstadoPedido.PENDIENTE))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solo se pueden eliminar pedidos que se encuentran pendientes."
            );
        }
        pedidoRepository.deleteById(id);
        return true;
    }

    //--------------------------Otros--------------------------//
    private void cargarItemsAlPedido(Pedido pedido, List<ViandaCantidadDTO> itemsDTO) {
        double total = 0.0;
        Emprendimiento emprendimientoDelPedido = pedido.getEmprendimiento();

        for (ViandaCantidadDTO item : itemsDTO) {
            Vianda vianda = viandaService.findEntityViandaById(item.getViandaId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "No se encontró una vianda con ID #" + item.getViandaId() + "."
                            )
                    );

            if (!vianda.getEmprendimiento().equals(emprendimientoDelPedido)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La vianda \"" + vianda.getNombreVianda() + "\" no pertenece al emprendimiento del pedido."
                );
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setVianda(vianda);
            detalle.setCantidad(item.getCantidad());

            detalle.setPrecioUnitario(vianda.getPrecio());
            detalle.setSubtotal(vianda.getPrecio() * item.getCantidad());

            pedido.agregarDetalle(detalle);
            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);
    }

    private void notificarCambio(Pedido pedido, String mensaje, Long idDestinatario) {
        notificacionService.createNotificacion(new NotificacionCreateDTO(
                idDestinatario,
                pedido.getEmprendimiento().getId(),
                mensaje,
                LocalDate.now()
        ));
    }
}