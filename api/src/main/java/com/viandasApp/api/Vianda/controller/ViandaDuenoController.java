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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Viandas - Dueño")
@RequestMapping("/api/dueno/viandas")
@RequiredArgsConstructor
public class ViandaDuenoController {

    private final ViandaService viandasService;

    //--------------------------Create--------------------------//
     @Operation(
            summary = "Crear una nueva vianda",
            description = "Crea una nueva vianda asociada al emprendimiento del dueño autenticado",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vianda creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createVianda(@Valid @ModelAttribute ViandaCreateDTO dto) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        ViandaDTO viandaCreada = viandasService.createVianda(dto, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda creada correctamente:", viandaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /// Usamos @ModelAttribute en lugar de @RequestBody porque ahora tenemos un archivo junto con datos.

    //--------------------------Read--------------------------//
     @Operation(
            summary = "Obtener viandas por emprendimiento",
            description = "Obtiene una lista de viandas filtradas por emprendimiento y otros criterios",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Viandas obtenidas correctamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
                    @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasByEmprendimiento(@Valid @ModelAttribute FiltroViandaDTO filtro, @PathVariable Long idEmprendimiento) {
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ViandaDTO> resultados = viandasService.getViandasByEmprendimiento(filtro, idEmprendimiento, usuarioLogueado);
        return ResponseEntity.ok(resultados);
    }

    @Operation(
            summary = "Obtener vianda por ID",
            description = "Obtiene una vianda específica por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<ViandaDTO> vianda = viandasService.findViandaById(id, usuario);
        return ResponseEntity.ok(vianda);
    }

    //--------------------------Update--------------------------//
    
   @Operation(
            summary = "Actualizar una vianda",
            description = "Actualiza los detalles de una vianda existente",
            security = @SecurityRequirement(name = "bearer-jwt")
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
    public ResponseEntity<Map<String, Object>> updateVianda(@PathVariable Long id, @Valid @RequestBody ViandaUpdateDTO dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<ViandaDTO> viandaActualizado = viandasService.updateVianda(id, dto, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda actualizada correctamente:", viandaActualizado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/id/{id}/imagen")
    public ResponseEntity<ViandaDTO> updateImageVianda(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) {
         Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                 .getAuthentication().getPrincipal();

         ViandaDTO viandaActualizada =viandasService.updateImagenVianda(id, image, usuario);
         return ResponseEntity.ok(viandaActualizada);
    }

    //--------------------------Delete--------------------------//
    @Operation(
            summary = "Eliminar una vianda",
            description = "Elimina una vianda existente por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vianda eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deleteVianda(@PathVariable Long id) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();

        viandasService.deleteVianda(id, usuario);
        response.put("message", "Vianda eliminada correctamente");
        return ResponseEntity.ok(response);
    }
}