package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.PedidoUpdateViandasDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
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
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
public class PedidoAdminController {
    private final PedidoService pedidoService;

    //--------------------------Create--------------------------//
    @PostMapping
    public ResponseEntity<?> createPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        PedidoDTO pedidoCrear = pedidoService.createPedido(pedidoCreateDTO, autenticado);
        Map<String, Object> response = new HashMap<>();
        response.put("Pedido creado correctamente",pedidoCrear);
        return ResponseEntity.ok(response);
    }

    //--------------------------Read--------------------------//
    @GetMapping
    public ResponseEntity<?> getAllPedidos() {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getPedidoPorId(@PathVariable Long id) {
        Optional<PedidoDTO> pedido = pedidoService.getPedidoById(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosDeUsuario(@PathVariable Long idUsuario) {
        List<PedidoDTO> pedido = pedidoService.getAllPedidosByUsuarioId(idUsuario);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByEmprendimiento(idEmprendimiento, autenticado);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorEmprendimientoYUsuario(@PathVariable Long idEmprendimiento,
                                                                 @PathVariable Long idUsuario) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, idUsuario, autenticado);

        return ResponseEntity.ok(pedidos);
    }
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getPedidosPorEstado(@PathVariable EstadoPedido estado) {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> getPedidosPorFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByFecha(fecha);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/fecha/{fecha}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorFechaAndUsuarioId(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                            @PathVariable Long idUsuario) {

        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByFechaAndUsuarioId(fecha,idUsuario);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/fecha/{fecha}/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorFechaAndEmprendimiento(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                                 @PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedidos = pedidoService.getAllPedidosByFechaAndEmprendimientoId(fecha, idEmprendimiento, autenticado);
        return ResponseEntity.ok(pedidos);
    }

    //--------------------------Update--------------------------//
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {

        Optional<PedidoDTO> pedidoExistente = pedidoService.getPedidoById(id);
        Map<String, Object> response = new HashMap<>();

        Optional<PedidoDTO> pedidoActualizar = pedidoService.updatePedidoAdmin(id, updatePedidoDTO);
        response.put("Pedido actualizado correctamente",pedidoActualizar);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/viandas")
    public ResponseEntity<?> updateViandasPedido(@PathVariable Long id, @Valid @RequestBody PedidoUpdateViandasDTO dto) {
        Map<String, Object> response = new HashMap<>();

        Optional<PedidoDTO> pedidoActualizar = pedidoService.updateViandasPedidoAdmin(id, dto);
        response.put("Pedido actualizado correctamente",pedidoActualizar);
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>>  deletePedido(@PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        pedidoService.deletePedidoAdmin(id);
        response.put("message", "Pedido eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}