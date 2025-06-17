package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Pedido.dto.*;
import com.viandasApp.api.Pedido.model.DetallePedido;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.service.ViandaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioServiceImpl usuarioService;
    private final EmprendimientoServiceImpl emprendimientoService;
    private final ViandaServiceImpl viandaService;


    //  admin y cliente
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

            if (updatePedidoDTO.getEstado() != null) {

                if ( updatePedidoDTO.getEstado().equals(EstadoPedido.CANCELADO) ){
                    pedido.setEstado(updatePedidoDTO.getEstado());
                }else throw new RuntimeException("El cliente solo puede CANCELAR el pedido.");

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
    public boolean deletePedido(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFecha(LocalDate fecha) {
        return pedidoRepository.findByFecha(fecha).stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
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
        return usuarioService.findById(idUsuario)
                .map(usuario -> pedidoRepository.findByUsuarioId(usuario.getId()).stream()
                        .map(PedidoDTO::new)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }



}