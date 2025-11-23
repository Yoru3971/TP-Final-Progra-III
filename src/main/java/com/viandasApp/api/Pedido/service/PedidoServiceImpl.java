package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
import com.viandasApp.api.Notificacion.service.NotificacionServiceImpl;
import com.viandasApp.api.Pedido.dto.*;
import com.viandasApp.api.Pedido.model.DetallePedido;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.service.ViandaServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final NotificacionServiceImpl notificacionService;

    //--------------------------Create--------------------------//
    @Transactional
    @Override
    public PedidoDTO createPedido(PedidoCreateDTO pedidoCreateDTO, Usuario usuarioLogueado) {

        if (usuarioLogueado.getRolUsuario().equals(RolUsuario.CLIENTE) && !usuarioLogueado.getId().equals(pedidoCreateDTO.getClienteId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no tiene permiso para crear pedidos para otro cliente");
        }

        Usuario cliente = usuarioService.findEntityById(pedidoCreateDTO.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!cliente.getRolUsuario().equals(RolUsuario.CLIENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario no es un cliente.");
        }

        Emprendimiento emprendimientoPedido = emprendimientoService.findEntityById(pedidoCreateDTO.getEmprendimientoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado"));

        Pedido pedido = DTOtoEntity(pedidoCreateDTO, cliente, emprendimientoPedido, viandaService);

        Pedido guardado = pedidoRepository.save(pedido);
        //Como este metodo lo usa el cliente, notificamos al dueño del emprendimiento

        Long idDuenoEmprendimiento = guardado.getEmprendimiento().getUsuario().getId();

        notificarCambio(
                guardado,
                "Te hicieron un nuevo pedido para tu emprendimiento '" + guardado.getEmprendimiento().getNombreEmprendimiento() +
                        "' para la fecha '" + guardado.getFechaEntrega() +
                        "', se registró con el ID #" + guardado.getId(),
                idDuenoEmprendimiento
        );
        return new PedidoDTO(guardado);
    }

    //--------------------------Read--------------------------//
    @Override
    public List<PedidoDTO> getAllPedidos() {
        List<PedidoDTO> pedidos = pedidoRepository.findAll()
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos.");
        }

        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEmprendimiento(Long idEmprendimiento, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado."));

        if (usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) && !(emprendimiento.getUsuario().getId().equals(usuarioLogueado.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El emprendimiento no pertenece al usuario logueado.");
        }

        List<PedidoDTO> pedidos = pedidoRepository.findByEmprendimientoId(idEmprendimiento)
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos para el emprendimiento con ID: " + idEmprendimiento);
        }

        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEmprendimientoAndUsuario(Long idEmprendimiento, Long idUsuario, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado."));

        Usuario usuario = usuarioService.findEntityById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if (usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) && !emprendimiento.getUsuario().equals(usuarioLogueado)) {
            throw new RuntimeException("El emprendimiento no pertenece al usuario logueado.");
        }


        List<PedidoDTO> pedidos = pedidoRepository.findByEmprendimientoIdAndUsuarioId(idEmprendimiento, idUsuario)
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos para el emprendimiento con ID: " + idEmprendimiento);
        }

        return pedidos;
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id) {
        Optional<PedidoDTO> pedido = pedidoRepository.findById(id)
                .map(PedidoDTO::new);

        if (pedido.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el pedido con ID: " + id);
        }

        return pedido;
    }

    @Override
    public List<PedidoDTO> getAllPedidosDueno(Long idDueno) {
        List<PedidoDTO> pedidos = pedidoRepository.findByEmprendimientoUsuarioId(idDueno)
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tenés pedidos todavía.");
        }

        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByUsuarioId(Long idUsuario) {

        Optional<UsuarioDTO> usuario = usuarioService.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + idUsuario);
        }

        List<PedidoDTO> pedidos = pedidoRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tenés pedidos todavía.");
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado) {
        List<PedidoDTO> pedidos = pedidoRepository.findByEstado(estado)
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos con el estado: " + estado);
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFecha(LocalDate fecha) {
        List<PedidoDTO> pedidos = pedidoRepository.findByFechaEntrega(fecha)
                .stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos con la fecha: " + fecha);
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFechaAndUsuarioId(LocalDate fecha, Long idUsuario) {
        Optional<UsuarioDTO> usuario = usuarioService.findById(idUsuario);

        if (usuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }

        List<PedidoDTO> pedidos = pedidoRepository.findByFechaEntregaAndUsuarioId(fecha, idUsuario).stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos para la fecha y usuario indicados.");
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFechaAndEmprendimientoId(LocalDate fecha, Long idEmprendimiento, Usuario usuarioLogueado) {
        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado."));

        if (usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) && !emprendimiento.getUsuario().equals(usuarioLogueado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El emprendimiento no pertenece al usuario logueado.");
        }

        List<PedidoDTO> pedidos = pedidoRepository.findByFechaEntregaAndEmprendimientoId(fecha, idEmprendimiento).stream()
                .map(PedidoDTO::new)
                .toList();

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pedidos para la fecha y emprendimiento indicados.");
        }
        return pedidos;
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<PedidoDTO> updatePedidoAdmin(Long id, UpdatePedidoDTO updatePedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el pedido con ID " + id + "."));

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró el pedido con ID " + id + "."));

        // --- Validar que el dueño sea realmente dueño del emprendimiento ---
        if (!pedido.getEmprendimiento().getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El emprendimiento del pedido no pertenece al usuario logueado.");
        }

        // --- Solo se puede actualizar si está pendiente ---
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Solo se pueden actualizar pedidos pendientes.");
        }

        boolean cambioEstado = false;


        // CAMBIO DE ESTADO (dueño acepta o rechaza)

        if (updatePedidoDTO.getEstado() == EstadoPedido.ACEPTADO ||
                updatePedidoDTO.getEstado() == EstadoPedido.RECHAZADO) {
            pedido.setEstado(updatePedidoDTO.getEstado());
            cambioEstado = true;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El dueño del emprendimiento solo puede aceptar o rechazar el pedido.");
        }

        // DUEÑO NO PUEDE CAMBIAR FECHA
        if (updatePedidoDTO.getFechaEntrega() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El dueño no puede modificar la fecha de entrega.");
        }

        Pedido actualizado = pedidoRepository.save(pedido);


        // Mensaje para el cliente
        String mensaje = "Tu pedido #" + actualizado.getId()
                + ", del emprendimiento '" + pedido.getEmprendimiento().getNombreEmprendimiento() + "'";

        if (cambioEstado) {
            mensaje += " cambió de estado a '" + actualizado.getEstado() + "'";
        } else {
            mensaje += " fue actualizado";
        }

        // Notificar al cliente
        notificarCambio(actualizado, mensaje, actualizado.getUsuario().getId());

        return Optional.of(new PedidoDTO(actualizado));
    }


    @Transactional
    @Override
    public Optional<PedidoDTO> updatePedidoCliente(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró el pedido con ID " + id + "."));

        // Validar pertenencia
        if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El pedido que se quiere actualizar no pertenece al usuario logueado.");
        }

        // Validar Estado PENDIENTE
        if (!pedido.getEstado().equals(EstadoPedido.PENDIENTE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se puede modificar un pedido pendiente.");
        }

        boolean cambioFecha = false;
        boolean cambioEstado = false;
        LocalDate hoy = LocalDate.now();

        // Logica para cambiar la fecha
        if (updatePedidoDTO.getFechaEntrega() != null) {

            // REGLA: No se puede cambiar si la entrega es mañana
            if (pedido.getFechaEntrega().equals(hoy.plusDays(1))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se puede cambiar la fecha porque la entrega es mañana. Contactá al emprendimiento.");
            }

            // REGLA: La nueva fecha no puede ser anterior a HOY
            if (updatePedidoDTO.getFechaEntrega().isBefore(hoy)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La nueva fecha no puede ser en el pasado.");
            }

            // REGLA SOLICITADA: La nueva fecha no puede ser anterior a la fecha ACTUAL del pedido.
            if (updatePedidoDTO.getFechaEntrega().isBefore(hoy.plusDays(2))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El cambio de fecha requiere al menos 48hs de anticipación. La fecha más próxima posible es: " + hoy.plusDays(2));
            }

            pedido.setFechaEntrega(updatePedidoDTO.getFechaEntrega());
            cambioFecha = true;
        }

        // Logica de cancelacion
        if (updatePedidoDTO.getEstado() != null) {

            // El cliente solo puede cancelar
            if (updatePedidoDTO.getEstado() != EstadoPedido.CANCELADO) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El cliente solo puede cancelar el pedido.");
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
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada"));

                if (!vianda.getEmprendimiento().equals(emprendimientoPedido)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las viandas deben pertenecer al mismo emprendimiento del pedido.");
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden actualizar pedidos que se encuentran PENDIENTES.");
            }

            if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido que se quiere actualizar no pertenece al usuario logueado.");
            }

            pedido.getViandas().clear();
            Emprendimiento emprendimientoPedido = pedido.getEmprendimiento();

            Double total = 0.0;
            for (PedidoUpdateViandasDTO.ViandaCantidadDTO vc : dto.getViandas()) {

                Vianda vianda = viandaService.findEntityViandaById(vc.getViandaId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada"));

                if (!vianda.getEmprendimiento().equals(emprendimientoPedido)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las viandas deben pertenecer al mismo emprendimiento del pedido.");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el pedido con el ID: " + id);
        }
        pedidoRepository.deleteById(id);
        return true;
    }

    @Transactional
    @Override
    public boolean deletePedidoCliente(Long id, Usuario usuarioLogueado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el pedido con el ID: " + id));

        if (!pedido.getUsuario().getId().equals(usuarioLogueado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido que se quiere eliminar no pertenece al usuario logueado.");
        }
        if (!(pedido.getEstado().equals(EstadoPedido.PENDIENTE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden eliminar pedidos que se encuentran PENDIENTES.");
        }
        pedidoRepository.deleteById(id);
        return true;
    }

    //--------------------------Otros--------------------------//
    private Pedido DTOtoEntity(PedidoCreateDTO dto, Usuario cliente, Emprendimiento emprendimiento, ViandaServiceImpl viandaService) {
        Pedido pedido = new Pedido();
        if (dto.getFechaEntrega() != null && dto.getFechaEntrega().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de entrega no puede ser anterior a la fecha de hoy.");
        }
        pedido.setFechaEntrega(dto.getFechaEntrega());
        pedido.setUsuario(cliente);
        pedido.setEmprendimiento(emprendimiento);

        double total = 0.0;
        for (ViandaCantidadDTO viandaDTO : dto.getViandas()) {
            Vianda vianda = viandaService.findEntityViandaById(viandaDTO.getViandaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada"));
            if (!vianda.getEmprendimiento().equals(emprendimiento)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las viandas deben pertenecer al mismo emprendimiento del pedido.");
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setVianda(vianda);
            detalle.setCantidad(viandaDTO.getCantidad());
            detalle.setPrecioUnitario(vianda.getPrecio());
            detalle.setSubtotal(vianda.getPrecio() * viandaDTO.getCantidad());
            total += detalle.getSubtotal();
            pedido.agregarDetalle(detalle);
        }
        pedido.setTotal(total);
        return pedido;
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