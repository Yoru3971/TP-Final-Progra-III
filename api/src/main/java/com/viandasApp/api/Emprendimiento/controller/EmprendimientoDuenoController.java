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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Emprendimientos - Dueño", description = "Controlador para gestionar emprendimientos desde el rol de dueño")
@RequestMapping("/api/dueno/emprendimientos")
public class EmprendimientoDuenoController {

    private final EmprendimientoService emprendimientoService;

    public EmprendimientoDuenoController(EmprendimientoService emprendimientoService) {

        this.emprendimientoService = emprendimientoService;
    }

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
    public ResponseEntity<?> createEmprendimiento(
            @Valid @RequestBody CreateEmprendimientoDTO createEmprendimientoDTO,
            BindingResult result
            ) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprendimientoGuardado);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

    }

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
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO) {

        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {

            Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO, usuario);

            if (emprendimientoActualizado.isEmpty()) {
                response.put("message", "El emprendimiento no se pudo actualizar.");
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
            } else {
                response.put("message", "Emprendimiento actualizado correctamente.");
                response.put("emprendimiento", emprendimientoActualizado.get());
                return ResponseEntity.ok(response);
            }


        } catch (EntityNotFoundException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

    }

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
    public ResponseEntity<Map<String, String>> deleteEmprendimiento(
            @PathVariable Long id
    ) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();
        boolean eliminado = emprendimientoService.deleteEmprendimiento(id, usuario);

        if (eliminado) {
            response.put("message", "Emprendimiento eliminado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Emprendimiento no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

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
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById(
            @PathVariable Long id) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);

        return emprendimiento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

        if (emprendimientos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }
}
