package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoService {

    PedidoDTO createPedido(PedidoCreateDTO dto);

    Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO);
    boolean deletePedido(Long id);

    PedidoDTO getPedidoById(Long id);

    List<PedidoDTO> getAllPedidos();
    List<PedidoDTO> getAllPedidosByClienteId(Long idCliente);
    List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado);
    List<PedidoDTO> getAllPedidosByFecha(LocalDateTime fecha);
}