package com.viandasApp.api.Reclamo.controller;

import com.viandasApp.api.Reclamo.model.Reclamo;
import com.viandasApp.api.Reclamo.service.ReclamoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Reclamos - Usuario")
@RequestMapping("/api/cliente/reclamos") // Aplica tanto para Cliente como Dueño
@RequiredArgsConstructor
public class ReclamoClienteDuenoController {

    private final ReclamoService reclamoService;

    //--------------------------Read (Mis Reclamos)--------------------------//
    @Operation(
            summary = "Obtener mis reclamos",
            description = "Permite al usuario logueado ver el historial de sus reclamos realizados",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reclamos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login")
    })
    @GetMapping
    public ResponseEntity<?> getMisReclamos() {
        // Obtenemos el usuario del contexto de seguridad (Igual que en tus Pedidos)
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // Usamos el email del usuario autenticado para buscar
        List<Reclamo> misReclamos = reclamoService.listarReclamosPorUsuario(autenticado.getEmail());

        return ResponseEntity.ok(misReclamos);
    }

}
