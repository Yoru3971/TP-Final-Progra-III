package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.dto.ViandaCantidadDTO;
import com.viandasApp.api.Pedido.model.DetallePedido;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.User.model.Usuario;
import com.viandasApp.api.User.repository.UsuarioRepository;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ViandaRepository viandaRepository;

    @Override
    public PedidoDTO createPedido(PedidoCreateDTO pedidoCreateDTO) {
        Usuario cliente = usuarioRepository.findById(pedidoCreateDTO.getCliente_id())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        for (ViandaCantidadDTO dto : pedidoCreateDTO.getViandas()) {
            Vianda vianda = viandaRepository.findById(dto.getViandaId())
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
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> pedidoRepository.findByUsuarioId(usuario.getId()).stream()
                        .map(PedidoDTO::new)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }
}