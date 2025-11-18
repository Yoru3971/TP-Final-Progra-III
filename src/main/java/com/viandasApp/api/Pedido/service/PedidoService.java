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
    //--------------------------Create--------------------------//
    PedidoDTO createPedido(PedidoCreateDTO dto, Usuario usuarioLogueado);

    //--------------------------Read--------------------------//
    List<PedidoDTO> getAllPedidos();
    List<PedidoDTO> getAllPedidosByEmprendimiento(Long idEmprendimiento, Usuario usuarioLogueado);
    List<PedidoDTO> getAllPedidosByEmprendimientoAndUsuario(Long idEmprendimiento,Long idUsuario, Usuario usuarioLogueado);
    Optional<PedidoDTO> getPedidoById(Long id);
    List<PedidoDTO> getAllPedidosDueno(Long idDueno);
    List<PedidoDTO> getAllPedidosByUsuarioId(Long idUsuario);
    List<PedidoDTO> getAllPedidosByEstado(EstadoPedido estado);
    List<PedidoDTO> getAllPedidosByFecha(LocalDate fecha);
    List<PedidoDTO> getAllPedidosByFechaAndUsuarioId(LocalDate fecha, Long idUsuario);
    List<PedidoDTO> getAllPedidosByFechaAndEmprendimientoId(LocalDate fecha, Long idEmprendimiento, Usuario usuarioLogueado);

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