package com.viandasApp.api.Pedido.service;


import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Override
    public PedidoDTO crearPedido(PedidoCreateDTO dto) {
        return null;
    }

    @Override
    public PedidoDTO cambiarEstado(Long idPedido, EstadoPedido nuevoEstado) {
        return null;
    }

    @Override
    public List<PedidoDTO> listarPedidosPorCliente(Long idCliente) {
        return List.of();
    }
}