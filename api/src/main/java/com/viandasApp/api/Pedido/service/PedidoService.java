package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    PedidoDTO createPedido(PedidoCreateDTO dto);
    Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO);
    boolean deletePedido(Long id);


    Optional<PedidoDTO> getPedidoById(Long id);
    List<PedidoDTO> getAllPedidos();
    List<PedidoDTO> getAllPedidosByUsuarioId(Long idUsuario);
    List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado);
    List<PedidoDTO> getAllPedidosByFecha(LocalDate fecha);
}