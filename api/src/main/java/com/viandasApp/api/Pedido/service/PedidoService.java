package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;

import java.util.List;

public interface PedidoService {

    PedidoDTO crearPedido(PedidoCreateDTO dto);

    PedidoDTO obtenerPedidoPorId(Long id);

    List<PedidoDTO> getAllPedidos();

    List<PedidoDTO> listarPedidosPorCliente(Long idCliente);
}