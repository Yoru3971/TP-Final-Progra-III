package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@RequestBody @Valid PedidoCreateDTO pedidoCreateDTO) {
        PedidoDTO pedidoDTO = pedidoService.createPedido(pedidoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos(){

        List<PedidoDTO> pedido = pedidoService.getAllPedidos();

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPedidoPorId(@PathVariable Long id) {
        PedidoDTO pedidoDTO = pedidoService.getPedidoById(id);
        return ResponseEntity.ok(pedidoDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosDeUsuario(@PathVariable Long idCliente) {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByClienteId(idCliente);
        return ResponseEntity.ok(pedidos);
    }
}