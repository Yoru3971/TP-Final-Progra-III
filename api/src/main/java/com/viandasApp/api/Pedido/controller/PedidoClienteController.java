package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.PedidoUpdateViandasDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.service.PedidoService;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente/pedidos")
@RequiredArgsConstructor
public class PedidoClienteController {
    private final PedidoService pedidoService;

    //--------------------------Create--------------------------//
    @PostMapping
    public ResponseEntity<?> createPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        PedidoDTO pedidoCreado = pedidoService.createPedido(pedidoCreateDTO, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Pedido creado correctamente:", pedidoCreado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Read--------------------------//
    @GetMapping
    public ResponseEntity<?> getPedidosPropios() {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByUsuarioId(autenticado.getId());
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, autenticado.getId(), autenticado);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> getPedidosPorFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndUsuarioId(fecha, autenticado.getId());
        return ResponseEntity.ok(pedido);
    }

    //--------------------------Update--------------------------//
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoActualizado = pedidoService.updatePedidoCliente(id, updatePedidoDTO, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Pedido actualizado correctamente:", pedidoActualizado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/viandas")
    public ResponseEntity<?> updateViandasPedido(@PathVariable Long id, @Valid @RequestBody PedidoUpdateViandasDTO dto) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoActualizado = pedidoService.updateViandasPedidoCliente(id, dto, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Pedido actualizado correctamente:", pedidoActualizado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>>  deletePedido(@PathVariable Long id) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();

        pedidoService.deletePedidoCliente(id, autenticado);
        response.put("message", "Pedido eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}
