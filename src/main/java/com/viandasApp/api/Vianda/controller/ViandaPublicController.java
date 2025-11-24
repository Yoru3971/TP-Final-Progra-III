package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/viandas")
@Tag(name = "Viandas - Público")
@RequiredArgsConstructor
public class ViandaPublicController {

    private final ViandaService viandasService;

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener viandas disponibles por emprendimiento",
            description = "Obtiene la lista de viandas disponibles para el emprendimiento especificado por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viandas encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento o viandas no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasDisponiblesByEmprendimiento(@Valid @ModelAttribute FiltroViandaDTO filtro, @PathVariable Long idEmprendimiento) {
        List<ViandaDTO> resultados = viandasService.getViandasDisponiblesByEmprendimiento(filtro, idEmprendimiento);
        return ResponseEntity.ok(resultados);
    }

    @Operation(
            summary = "Obtener vianda por ID",
            description = "Obtiene la información de una vianda específica por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vianda encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<ViandaDTO> vianda = viandasService.findViandaByIdPublic(id);
        return ResponseEntity.ok(vianda);
    }
}