package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.PedidoUpdateViandasDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Usuario.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    PedidoDTO createPedido(PedidoCreateDTO dto, Usuario usuarioLogueado);
    Optional<PedidoDTO> updatePedidoAdmin(Long id, UpdatePedidoDTO updatePedidoDTO);
    Optional<PedidoDTO> updatePedidoDueno(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado);
    Optional<PedidoDTO> updatePedidoCliente(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado);
    Optional<PedidoDTO> updateViandasPedidoAdmin(Long pedidoId, PedidoUpdateViandasDTO dto);
    Optional<PedidoDTO> updateViandasPedidoCliente(Long pedidoId, PedidoUpdateViandasDTO dto, Usuario usuarioLogueado);
    boolean deletePedido(Long id);
    Optional<PedidoDTO> getPedidoById(Long id);
    List<PedidoDTO> getAllPedidos();
    List<PedidoDTO> getAllPedidosByUsuarioId(Long idUsuario);
    List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado);
    List<PedidoDTO> getAllPedidosByFecha(LocalDate fecha);
}