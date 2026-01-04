package com.viandasApp.api.Reclamo.controller;

import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import com.viandasApp.api.Reclamo.service.ReclamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Reclamos - Admin")
@RequestMapping("/api/admin/reclamos")
@RequiredArgsConstructor
public class ReclamoAdminController {

    private final ReclamoService reclamoService;

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener todos los reclamos",
            description = "Obtiene una lista de todos los reclamos registrados en el sistema, ordenados por fecha",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reclamos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, requiere rol ADMIN")
    })
    @GetMapping
    public ResponseEntity<List<Reclamo>> getAllReclamos() {
        List<Reclamo> reclamos = reclamoService.listarTodosLosReclamos();
        return ResponseEntity.ok(reclamos);
    }

    //--------------------------Read (Por ID)--------------------------//
    @Operation(
            summary = "Obtener reclamo por ID",
            description = "Obtiene el detalle de un reclamo específico",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reclamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Reclamo no encontrado")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getReclamoPorId(@PathVariable Long id) {
        Optional<Reclamo> reclamo = reclamoService.obtenerReclamoPorId(id);
        if (reclamo.isPresent()) {
            return ResponseEntity.ok(reclamo.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //--------------------------Read (Por Estado)--------------------------//
    @Operation(
            summary = "Filtrar reclamos por estado",
            description = "Obtiene los reclamos filtrados por su estado (PENDIENTE, RESUELTO, etc)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reclamo>> getReclamosPorEstado(@PathVariable EstadoReclamo estado) {
        List<Reclamo> reclamos = reclamoService.listarReclamosPorEstado(estado);
        return ResponseEntity.ok(reclamos);
    }

    //--------------------------Update (Cambiar Estado)--------------------------//
    @Operation(
            summary = "Actualizar estado de un reclamo",
            description = "Permite cambiar el estado de un reclamo (ej: marcar como RESUELTO)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Reclamo no encontrado")
    })
    @PutMapping("/id/{id}/estado")
    public ResponseEntity<?> updateEstadoReclamo(@PathVariable Long id, @RequestParam EstadoReclamo nuevoEstado) {
        try {
            Reclamo actualizado = reclamoService.actualizarEstadoReclamo(id, nuevoEstado);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Estado actualizado correctamente");
            response.put("reclamo", actualizado);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}