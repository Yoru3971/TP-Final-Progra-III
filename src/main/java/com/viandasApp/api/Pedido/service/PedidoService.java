package com.viandasApp.api.Pedido.service;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.PedidoUpdateViandasDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    //--------------------------Create--------------------------//
    PedidoDTO createPedido(PedidoCreateDTO dto, Usuario usuarioLogueado);

    //--------------------------Read--------------------------//
    Page<PedidoDTO> buscarPedidos(Usuario usuarioLogueado, EstadoPedido estado, String nombreEmprendimiento, LocalDate desde, LocalDate hasta, Pageable pageable);
    Page<Pedido> buscarEntidadesPedidos(Usuario usuario, EstadoPedido estado, String nombreEmprendimiento, LocalDate desde, LocalDate hasta, Pageable pageable);
    Optional<PedidoDTO> getPedidoById(Long id, Usuario usuario);
    List<String> getNombresEmprendimientosFiltro(Usuario usuario);

    //--------------------------Update--------------------------//
    Optional<PedidoDTO> updatePedidoAdmin(Long id, UpdatePedidoDTO updatePedidoDTO);
    Optional<PedidoDTO> updatePedidoDueno(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado);
    Optional<PedidoDTO> updatePedidoCliente(Long id, UpdatePedidoDTO updatePedidoDTO, Usuario usuarioLogueado);
    Optional<PedidoDTO> updateViandasPedidoAdmin(Long pedidoId, PedidoUpdateViandasDTO dto);
    Optional<PedidoDTO> updateViandasPedidoCliente(Long pedidoId, PedidoUpdateViandasDTO dto, Usuario usuarioLogueado);

    //--------------------------Delete--------------------------//
    boolean deletePedidoAdmin(Long id);
    boolean deletePedidoCliente(Long id, Usuario usuarioLogueado);
}