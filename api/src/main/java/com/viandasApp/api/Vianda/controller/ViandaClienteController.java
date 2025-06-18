package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente/viandas")
public class ViandaClienteController {
    private final ViandaService viandasService;

    public ViandaClienteController(ViandaService viandasService) {
        this.viandasService = viandasService;
    }

    @Operation(
            summary = "Obtener viandas disponibles por emprendimiento",
            description = "Obtiene la lista de viandas disponibles por emprendimiento para el cliente autenticado",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id-emprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasDisponiblesByEmprendimiento(
            @Valid @ModelAttribute FiltroViandaDTO filtro,
            @PathVariable Long idEmprendimiento) {
        List<ViandaDTO> resultados = viandasService.getViandasDisponiblesByEmprendimiento(filtro, idEmprendimiento);
        return ResponseEntity.ok(resultados);
    }

    // ---------------------------------------------------------------------------------------

    @Operation(
            summary = "Obtener vianda por ID",
            description = "Obtiene la vianda correspondiente al ID proporcionado para el cliente autenticado",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vianda encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> findById(@PathVariable Long id, Usuario usuario) {
        return viandasService.findViandaById(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
