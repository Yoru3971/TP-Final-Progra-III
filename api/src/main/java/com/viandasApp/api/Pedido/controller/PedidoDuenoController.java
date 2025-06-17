package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.service.PedidoService;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dueno/pedidos")
@PreAuthorize("hasAuthority('ROLE_DUENO')")
@RequiredArgsConstructor
public class PedidoDuenoController {

    private final PedidoService pedidoService;

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoExistente = pedidoService.getPedidoById(id);
        Map<String, Object> response = new HashMap<>();

        if(pedidoExistente.isPresent()){
            pedidoService.updatePedidoDueno(id, updatePedidoDTO, autenticado);
            response.put("message", "Pedido actualizado correctamente");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimiento(idEmprendimiento, autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorEmprendimientoYUsuario(@PathVariable Long idEmprendimiento,
                                                                 @PathVariable Long idUsuario) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, idUsuario, autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/fecha/{fecha}/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorFechaAndEmprendimiento(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                                 @PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndEmprendimiento(fecha, idEmprendimiento, autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
