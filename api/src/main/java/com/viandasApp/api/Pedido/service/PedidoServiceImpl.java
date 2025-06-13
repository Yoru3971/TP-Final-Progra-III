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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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
            Vianda vianda = viandaRepository.findById(dto.getVianda_id())
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
        return Optional.empty();
    }

    @Override
    public boolean deletePedido(Long id) {
        return false;
    }

    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public PedidoDTO getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return new PedidoDTO(pedido);
    }

    @Override
    public List<PedidoDTO> getAllPedidosByClienteId(Long idCliente) {
        Usuario usuario = usuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByClienteId(usuario.getId());
        return pedidos.stream().map(PedidoDTO::new).toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado) {
        return pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getEstado().equals(estado))
                .map(PedidoDTO::new)
                .toList();
    }

    @Override
    public List<PedidoDTO> getAllPedidosByFecha(LocalDateTime fecha) {
        return List.of();
    }
}