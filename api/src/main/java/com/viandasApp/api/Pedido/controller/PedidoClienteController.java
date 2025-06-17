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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente/pedidos")
@PreAuthorize("hasAuthority('ROLE_CLIENTE')")
@RequiredArgsConstructor
public class PedidoClienteController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> createPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO, BindingResult result) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoDTO pedidoDTO = pedidoService.createPedido(pedidoCreateDTO, autenticado);
        if (pedidoDTO == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido. Por favor, intente nuevamente.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoExistente = pedidoService.getPedidoById(id);
        Map<String, Object> response = new HashMap<>();

        if(pedidoExistente.isPresent()){
            pedidoService.updatePedidoCliente(id, updatePedidoDTO, autenticado);
            response.put("message", "Pedido actualizado correctamente");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/viandas")
    public ResponseEntity<?> updateViandasPedido(@PathVariable Long id, @Valid @RequestBody PedidoUpdateViandasDTO dto, BindingResult result) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<PedidoDTO> actualizado = pedidoService.updateViandasPedidoCliente(id, dto, autenticado);
        if (actualizado.isPresent()) {
            return ResponseEntity.ok(actualizado.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pedido o vianda no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>>  deletePedido(@PathVariable Long id) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoEliminar = pedidoService.getPedidoById(id);
        Map<String, String> response = new HashMap<>();

        if(pedidoEliminar.isPresent()){
            pedidoService.deletePedidoCliente(id, autenticado);
            response.put("message", "Pedido eliminado correctamente");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getPedidosPropios() {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByUsuarioId(autenticado.getId());

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, autenticado.getId(), autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> getPedidosPorFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndUsuarioId(fecha, autenticado.getId());

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
