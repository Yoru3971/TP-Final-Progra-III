package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;

import java.util.List;

public interface PedidoService {

    PedidoDTO crearPedido(PedidoCreateDTO dto);

    PedidoDTO cambiarEstado(Long idPedido, EstadoPedido nuevoEstado);

    List<PedidoDTO> listarPedidosPorCliente(Long idCliente);
}