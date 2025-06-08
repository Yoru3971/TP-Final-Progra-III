package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDTO crearPedido(@RequestBody PedidoCreateDTO dto) {
        return pedidoService.crearPedido(dto);
    }

    @PutMapping("/{id}/estado")
    public PedidoDTO cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado
    ) {
        return pedidoService.cambiarEstado(id, estado);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<PedidoDTO> listarPedidosCliente(@PathVariable Long idCliente) {
        return pedidoService.listarPedidosPorCliente(idCliente);
    }
}