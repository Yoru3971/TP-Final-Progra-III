package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    @Override
    public PedidoDTO createPedido(PedidoCreateDTO pedidoCreateDTO, Usuario usuarioLogueado) {

        if ( usuarioLogueado.getRolUsuario().equals(RolUsuario.CLIENTE) && !usuarioLogueado.getId().equals(pedidoCreateDTO.getClienteId())){
            throw new RuntimeException("El usuario no tiene permiso para crear pedidos para otro cliente");
        }

        Usuario cliente = usuarioService.findEntityById(pedidoCreateDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(pedidoCreateDTO.getEmprendimientoId())
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(cliente);
        pedido.setEmprendimiento(emprendimiento);

        for (ViandaCantidadDTO dto : pedidoCreateDTO.getViandas()) {
            Vianda vianda = viandaService.findEntityViandaById(dto.getViandaId())
                    .orElseThrow(() -> new RuntimeException("Vianda no encontrada"));

            DetallePedido detalle = new DetallePedido();
            detalle.setVianda(vianda);
            detalle.setCantidad(dto.getCantidad());

            pedido.agregarDetalle(detalle); // Asocia el detalle al pedido
        }

        Pedido guardado = pedidoRepository.save(pedido);
        return new PedidoDTO(guardado);
    }

    @Override
    public Optional<PedidoDTO> updatePedidoAdmin(Long id, UpdatePedidoDTO updatePedidoDTO) {
        //  Sin restricciones
        return pedidoRepository.findById(id).map(pedido -> {
            if (updatePedidoDTO.getEstado() != null) {
                pedido.setEstado(updatePedidoDTO.getEstado());
            }
            if (updatePedidoDTO.getFecha() != null) {
                pedido.setFechaEntrega(updatePedidoDTO.getFecha());
            }
            Pedido actualizado = pedidoRepository.save(pedido);
            return new PedidoDTO(actualizado);
        });
    }

    @Override
    public Optional<PedidoDTO> updatePedidoDueno(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado) {

        Pedido pedido = pedidoRepository.findById(id)
                            .orElseThrow(()-> new RuntimeException("No se encontró el pedido con ID " + id + "."));

        //  Solo se puede actualizar un pedido si está pendiente
        if ( pedido.getEstado().equals(EstadoPedido.PENDIENTE) ){

                if ( !pedido.getEmprendimiento().getUsuario().equals(usuarioLogueado) ){
                    throw new RuntimeException("El emprendimiento del pedido que se quiere actualizar no pertenece al usuario logueado.");
                }

                if ( updatePedidoDTO.getEstado().equals(EstadoPedido.ACEPTADO) || updatePedidoDTO.getEstado().equals(EstadoPedido.RECHAZADO) ){

                    pedido.setEstado(updatePedidoDTO.getEstado());
                    Pedido actualizado = pedidoRepository.save(pedido);
                    return Optional.of(new PedidoDTO(actualizado));

                }else throw new RuntimeException("El dueño del emprendimiento solo puede aceptar o rechazar el pedido.");

        }else throw new RuntimeException("No se puede actualizar un pedido que se encuentra aceptado o rechazado.");

    }

    @Override
    public Optional<PedidoDTO> updatePedidoCliente(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado){

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se encontró el pedido con ID " + id + "."));

        //  Solo se puede actualizar un pedido si está pendiente
        if ( pedido.getEstado().equals(EstadoPedido.PENDIENTE) ){

            if ( !pedido.getUsuario().equals(usuarioLogueado) ){
                throw new RuntimeException("El pedido que se quiere actualizar no pertenece al usuario logueado.");
            }

            if (updatePedidoDTO.getFecha() != null) {
                if ( updatePedidoDTO.getFecha().isBefore(LocalDate.now()) ){
                    throw new RuntimeException("La fecha de entrega no puede ser anterior a la fecha de hoy.");
                }
                pedido.setFechaEntrega(updatePedidoDTO.getFecha());
            }
            Pedido actualizado = pedidoRepository.save(pedido);
            return Optional.of(new PedidoDTO(actualizado));

        }else throw new RuntimeException("No se puede actualizar un pedido que se encuentra aceptado o rechazado.");

    }

    @Override
    public Optional<PedidoDTO> updateViandasPedidoAdmin(Long pedidoId, PedidoUpdateViandasDTO dto) {
        return pedidoRepository.findById(pedidoId).flatMap(pedido -> {
            pedido.getViandas().clear();
            Emprendimiento emprendimientoPedido = pedido.getEmprendimiento();

            for (PedidoUpdateViandasDTO.ViandaCantidadDTO vc : dto.getViandas()) {
                Optional<Vianda> viandaOpt = viandaService.findEntityViandaById(vc.getViandaId());
                if (viandaOpt.isEmpty()) {
                    // Si alguna vianda no existe, retorna vacío como señal de error
                    return Optional.empty();
                }
                if ( !viandaOpt.get().getEmprendimiento().equals(emprendimientoPedido) ) {
                    throw new RuntimeException("Las viandas deben pertenecer al mismo emprendimiento del pedido.");
                }
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setVianda(viandaOpt.get());
                detalle.setCantidad(vc.getCantidad());
                pedido.agregarDetalle(detalle);
            }

            Pedido actualizado = pedidoRepository.save(pedido);
            return Optional.of(new PedidoDTO(actualizado));
        });
    }

    @Override
    public Optional<PedidoDTO> updateViandasPedidoCliente(Long pedidoId, PedidoUpdateViandasDTO dto, Usuario usuarioLogueado) {
        return pedidoRepository.findById(pedidoId).flatMap(pedido -> {

            if ( pedido.getEstado().equals(EstadoPedido.PENDIENTE) ){
                if ( !pedido.getUsuario().equals(usuarioLogueado) ){
                    throw new RuntimeException("El pedido que se quiere actualizar no pertenece al usuario logueado.");
                }

                pedido.getViandas().clear();
                Emprendimiento emprendimientoPedido = pedido.getEmprendimiento();

                for (PedidoUpdateViandasDTO.ViandaCantidadDTO vc : dto.getViandas()) {
                    Optional<Vianda> viandaOpt = viandaService.findEntityViandaById(vc.getViandaId());
                    if (viandaOpt.isEmpty()) {
                        // Si alguna vianda no existe, retorna vacío como señal de error
                        return Optional.empty();
                    }

                    if ( !viandaOpt.get().getEmprendimiento().equals(emprendimientoPedido) ) {
                        throw new RuntimeException("Las viandas deben pertenecer al mismo emprendimiento del pedido.");
                    }

                    DetallePedido detalle = new DetallePedido();
                    detalle.setPedido(pedido);
                    detalle.setVianda(viandaOpt.get());
                    detalle.setCantidad(vc.getCantidad());
                    pedido.agregarDetalle(detalle);
                }

                Pedido actualizado = pedidoRepository.save(pedido);
                return Optional.of(new PedidoDTO(actualizado));

            } else {
                throw new RuntimeException("Solo se pueden actualizar pedidos que se encuentran PENDIENTES.");
            }

        });
    }

    @Override
    public boolean deletePedidoAdmin(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePedidoCliente(Long id, Usuario usuarioLogueado) {

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if ( pedido.isPresent() ) {

            if ( !pedido.get().getUsuario().equals(usuarioLogueado) ){
                throw new RuntimeException("El pedido que se quiere eliminar no pertenece al usuario logueado.");
            }
            if ( !(pedido.get().getEstado().equals(EstadoPedido.PENDIENTE) || pedido.get().getEstado().equals(EstadoPedido.CARRITO)) ) {
                throw new RuntimeException("Solo se pueden eliminar pedidos que se encuentran PENDIENTES o en CARRITO.");
            }
            pedidoRepository.deleteById(id);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEmprendimiento(Long idEmprendimiento, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado."));

        if ( usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) && !emprendimiento.getUsuario().equals(usuarioLogueado) ){
            throw new RuntimeException("El emprendimiento no pertenece al usuario logueado.");
        }

        return pedidoRepository.findByEmprendimientoId(idEmprendimiento).stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEmprendimientoAndUsuario(Long idEmprendimiento, Long idUsuario, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado."));

        Usuario usuario = usuarioService.findEntityById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if ( usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) && !emprendimiento.getUsuario().equals(usuarioLogueado) ){
            throw new RuntimeException("El emprendimiento no pertenece al usuario logueado.");
        }

        return pedidoRepository.findByEmprendimientoIdAndUsuarioId(idEmprendimiento, idUsuario).stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id) {
        return pedidoRepository.findById(id)
                .map(PedidoDTO::new);
    }

    @Override
    public List<PedidoDTO> getAllPedidosByUsuarioId(Long idUsuario) {

        Optional<UsuarioDTO> usuario = usuarioService.findById(idUsuario);

        if ( usuario.isEmpty() ){
            throw new RuntimeException("Usuario no encontrado.");
        }else {
            return pedidoRepository.findByUsuarioId(usuario.get().getId()).stream()
                            .map(PedidoDTO::new)
                            .toList();
        }

    }

    @Override
    public List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFecha(LocalDate fecha) {
        return pedidoRepository.findByFechaEntrega(fecha).stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFechaAndUsuarioId(LocalDate fecha, Long idUsuario) {

        Optional<UsuarioDTO> usuario = usuarioService.findById(idUsuario);

        if ( usuario.isEmpty() ){
            throw new RuntimeException("Usuario no encontrado.");
        }else {
            return pedidoRepository.findByFechaEntregaAndUsuarioId(fecha, idUsuario).stream()
                    .map(PedidoDTO::new)
                    .toList();
        }
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFechaAndEmprendimiento(LocalDate fecha, Long idEmprendimiento, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado."));

        if ( usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) && !emprendimiento.getUsuario().equals(usuarioLogueado) ){
            throw new RuntimeException("El emprendimiento no pertenece al usuario logueado.");
        }

        return pedidoRepository.findByFechaEntregaAndEmprendimientoId(fecha, idEmprendimiento).stream()
                .map(PedidoDTO::new)
                .toList();
    }

}