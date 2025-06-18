package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Viandas - Dueño", description = "Controlador para gestionar viandas desde el rol de dueño")
@RequestMapping("/api/dueno/viandas")
public class ViandaDuenoController {
    private final ViandaService viandasService;

    public ViandaDuenoController(ViandaService viandasService) {
        this.viandasService = viandasService;
    }

    @Operation(
            summary = "Obtener viandas por emprendimiento",
            description = "Obtiene una lista de viandas filtradas por emprendimiento y otros criterios",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Viandas obtenidas correctamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
                    @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasByEmprendimiento(
            @Valid @ModelAttribute FiltroViandaDTO filtro,
            @PathVariable Long idEmprendimiento) {

        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ViandaDTO> resultados = viandasService.getViandasByEmprendimiento(filtro, idEmprendimiento, usuarioLogueado);
        return ResponseEntity.ok(resultados);
    }

    @Operation(
            summary = "Obtener vianda por ID",
            description = "Obtiene una vianda específica por su ID",
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
    public ResponseEntity<ViandaDTO> findById(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return viandasService.findViandaById(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------------------------------------------------------------------------------------

    @Operation(
            summary = "Crear una nueva vianda",
            description = "Crea una nueva vianda asociada al emprendimiento del dueño autenticado",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vianda creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ViandaDTO> createVianda(
            @Valid @RequestBody ViandaCreateDTO dto) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return new ResponseEntity<>(viandasService.createVianda(dto, autenticado), HttpStatus.CREATED);
    }


    @Operation(
            summary = "Actualizar una vianda",
            description = "Actualiza los detalles de una vianda existente",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vianda actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> updateVianda(
            @PathVariable Long id,
            @Valid @RequestBody ViandaUpdateDTO dto,
            Authentication authentication
    ) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        return viandasService.updateVianda(id, dto, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Eliminar una vianda",
            description = "Elimina una vianda existente por su ID",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vianda eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteVianda(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        return viandasService.deleteVianda(id, usuario)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : ResponseEntity.notFound().build();
    }
}