package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.service.PedidoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedidos - Dueño", description = "Controlador para gestionar pedidos desde el rol de dueño")
@RequestMapping("/api/dueno/pedidos")
@RequiredArgsConstructor
public class PedidoDuenoController {
    private final PedidoService pedidoService;

    //--------------------------Update--------------------------//
     @Operation(
            summary = "Actualizar pedido por ID",
            description = "Actualiza la información de un pedido específico por su ID",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
     @Operation(
            summary = "Obtener pedidos por emprendimiento",
            description = "Obtiene todos los pedidos asociados a un emprendimiento específico",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimiento(idEmprendimiento, autenticado);
        return ResponseEntity.ok(pedido);
    }

    @Operation(
            summary = "Obtener pedidos por emprendimiento y usuario",
            description = "Obtiene todos los pedidos asociados a un emprendimiento específico y un usuario",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorEmprendimientoYUsuario(@PathVariable Long idEmprendimiento,
                                                                 @PathVariable Long idUsuario) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, idUsuario, autenticado);
        return ResponseEntity.ok(pedido);
    }

    @Operation(
            summary = "Obtener pedidos por fecha y emprendimiento",
            description = "Obtiene todos los pedidos realizados en una fecha y emprendimiento específicos",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/fecha/{fecha}/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorFechaAndEmprendimiento(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                                 @PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndEmprendimientoId(fecha, idEmprendimiento, autenticado);
        return ResponseEntity.ok(pedido);
    }
}
