package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Emprendimientos - Dueño", description = "Controlador para gestionar emprendimientos desde el rol de dueño")
@RequestMapping("/api/dueno/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoDuenoController {
    private final EmprendimientoService emprendimientoService;
   
    //--------------------------Create--------------------------//
   @Operation(
            summary = "Crear un nuevo emprendimiento",
            description = "Permite a un dueño crear un nuevo emprendimiento asociado a su usuario.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Emprendimiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Entidad no encontrada, por ejemplo, usuario no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> createEmprendimiento(@Valid @RequestBody CreateEmprendimientoDTO createEmprendimientoDTO) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento creado correctamente:", emprendimientoGuardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener emprendimiento por ID",
            description = "Permite a un dueño obtener un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getEmprendimientoById(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);
        return ResponseEntity.ok(emprendimiento);
    }
  
    @Operation(
            summary = "Obtener emprendimientos propios",
            description = "Permite a un dueño obtener una lista de sus propios emprendimientos.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosPropios() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByUsuarioId(usuario.getId(), usuario);
        return ResponseEntity.ok(emprendimientos);
    }

    //--------------------------Update--------------------------//
    @Operation(
            summary = "Actualizar un emprendimiento por ID",
            description = "Permite a un dueño actualizar la información de un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(@PathVariable Long id, @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento actualizado correctamente:", emprendimientoActualizado);
        return ResponseEntity.ok(response);
    }
    
  //--------------------------Delete--------------------------//
     @Operation(
            summary = "Eliminar un emprendimiento por ID",
            description = "Permite a un dueño eliminar un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deleteEmprendimiento(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();

        emprendimientoService.deleteEmprendimiento(id, usuario);
        response.put("message", "Emprendimiento eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}
