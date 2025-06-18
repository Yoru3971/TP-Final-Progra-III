package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoDTO;
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
@RequestMapping("/api/dueno/pedidos")
@RequiredArgsConstructor
public class PedidoDuenoController {
    private final PedidoService pedidoService;

    //--------------------------Update--------------------------//
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoActualizado = pedidoService.updatePedidoDueno(id, updatePedidoDTO, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Pedido actualizado correctamente:", pedidoActualizado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Read--------------------------//
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimiento(idEmprendimiento, autenticado);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorEmprendimientoYUsuario(@PathVariable Long idEmprendimiento,
                                                                 @PathVariable Long idUsuario) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, idUsuario, autenticado);
        return ResponseEntity.ok(pedido);
    }
    @GetMapping("/fecha/{fecha}/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorFechaAndEmprendimiento(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                                 @PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndEmprendimientoId(fecha, idEmprendimiento, autenticado);
        return ResponseEntity.ok(pedido);
    }
}
