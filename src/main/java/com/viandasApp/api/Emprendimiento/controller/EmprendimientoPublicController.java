package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Emprendimientos - Público")
@RequestMapping("/api/public/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoPublicController {
    private final EmprendimientoService emprendimientoService;
 
    //--------------------------Read--------------------------//
     @Operation(
            summary = "Obtener todos los emprendimientos",
            description = "Devuelve una lista de todos los emprendimientos disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de emprendimientos obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })  
    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getAllEmprendimientos(){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getAllEmprendimientos();
        return ResponseEntity.ok(emprendimientos);
    }

    @Operation(
            summary = "Obtener emprendimiento por ID",
            description = "Devuelve un emprendimiento específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getEmprendimientoById (@PathVariable Long id, Usuario usuario){
        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);
        return ResponseEntity.ok(emprendimiento);
    }

    @Operation(
            summary = "Obtener emprendimientos por nombre",
            description = "Devuelve una lista de emprendimientos que coinciden con el nombre proporcionado",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos con ese nombre"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/nombre/{nombreEmprendimiento}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByNombre(@PathVariable String nombreEmprendimiento){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByNombre(nombreEmprendimiento);
        return ResponseEntity.ok(emprendimientos);
    }

    @Operation(
            summary = "Obtener emprendimientos por ciudad",
            description = "Devuelve una lista de emprendimientos que operan en la ciudad especificada",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos en esa ciudad"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByCiudad(@PathVariable String ciudad){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByCiudad(ciudad);
        return ResponseEntity.ok(emprendimientos);
    }
}
