package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Pedido.dto.*;
import com.viandasApp.api.Pedido.model.DetallePedido;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
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


    //  MÉTODOS DEL ADMIN

    @Override
    public PedidoDTO createPedido(PedidoCreateDTO pedidoCreateDTO) {

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
    public Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO) {
        return pedidoRepository.findById(id).map(pedido -> {
            if (updatePedidoDTO.getEstado() != null) {
                pedido.setEstado(updatePedidoDTO.getEstado());
            }
            if (updatePedidoDTO.getFecha() != null) {
                pedido.setFecha(updatePedidoDTO.getFecha());
            }
            Pedido actualizado = pedidoRepository.save(pedido);
            return new PedidoDTO(actualizado);
        });
    }

    @Override
    public Optional<PedidoDTO> updateViandasPedido(Long pedidoId, PedidoUpdateViandasDTO dto) {
        return pedidoRepository.findById(pedidoId).map(pedido -> {
            pedido.getViandas().clear();

            for (PedidoUpdateViandasDTO.ViandaCantidadDTO vc : dto.getViandas()) {
                Optional<Vianda> viandaOpt = viandaService.findEntityViandaById(vc.getViandaId());
                if (viandaOpt.isEmpty()) {
                    // Si alguna vianda no existe, retorna vacío como señal de error
                    return null;
                }
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setVianda(viandaOpt.get());
                detalle.setCantidad(vc.getCantidad());
                pedido.agregarDetalle(detalle);
            }

            Pedido actualizado = pedidoRepository.save(pedido);
            return new PedidoDTO(actualizado);
        }).filter(dtoResult -> dtoResult != null);
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

    //  MÉTODOS DEL DUEÑO

    //  MÉTODOS DEL CLIENTE


}