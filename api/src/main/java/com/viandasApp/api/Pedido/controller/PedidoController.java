package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.PedidoUpdateViandasDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoDTO pedidoDTO = pedidoService.createPedido(pedidoCreateDTO);
        if (pedidoDTO == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido. Por favor, intente nuevamente.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarPedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {
        Optional<PedidoDTO> pedidoExistente = pedidoService.getPedidoById(id);
        Map<String, Object> response = new HashMap<>();
        if(pedidoExistente.isPresent()){
            pedidoService.updatePedido(id, updatePedidoDTO);
            response.put("message", "Pedido actualizado correctamente");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/viandas")
    public ResponseEntity<?> actualizarViandasPedido(@PathVariable Long id, @Valid @RequestBody PedidoUpdateViandasDTO dto, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<PedidoDTO> actualizado = pedidoService.updateViandasPedido(id, dto);
        if (actualizado.isPresent()) {
            return ResponseEntity.ok(actualizado.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pedido o vianda no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>>  eliminarPedido(@PathVariable Long id) {

        Optional<PedidoDTO> pedidoEliminar = pedidoService.getPedidoById(id);
        Map<String, String> response = new HashMap<>();

        if(pedidoEliminar.isPresent()){
            pedidoService.deletePedido(id);
            response.put("message", "Pedido eliminado correctamente");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerPedidos() {
        List<PedidoDTO> pedido = pedidoService.getAllPedidos();

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
        Optional<PedidoDTO> pedido = pedidoService.getPedidoById(id);
        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        }
        else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPedidosDeUsuario(@PathVariable Long usuarioId) {
        List<PedidoDTO> pedido = pedidoService.getAllPedidosByUsuarioId(usuarioId);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable EstadoPedido estado) {
        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEstado(estado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFecha(fecha);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    // Métodos de respuesta genéricos, pueden ser utilizados para simplificar el manejo de respuestas
    // Se podrian almacenar en una clase utils para hacerlo reutilizable en otros controladores
//    private ResponseEntity<?> respuestaOk(Object body) {
//        return ResponseEntity.ok(body);
//    }
//
//    private ResponseEntity<?> respuestaNoEncontrado(String mensaje) {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", mensaje);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }

    // Ejemplo de como usarlo con un Optional (falta testear)
//    @GetMapping("/{id}")
//    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
//        return pedidoService.getPedidoById(id)
//                .map(this::respuestaOk)
//                .orElseGet(() -> respuestaNoEncontrado("Pedido no encontrado"));
//    }

    // Ejemplo de como usarlo con una lista (falta testear)
//    @GetMapping
//    public ResponseEntity<?> obtenerPedidos() {
//        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
//        return pedidos.isEmpty()
//                ? respuestaNoEncontrado("No se encontraron pedidos")
//                : respuestaOk(pedidos);
//    }

}