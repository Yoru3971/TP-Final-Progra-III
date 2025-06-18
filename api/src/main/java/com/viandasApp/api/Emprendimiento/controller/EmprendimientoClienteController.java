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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Emprendimientos - Cliente", description = "Controlador para gestionar emprendimientos desde el rol de cliente")
@RequestMapping("/api/cliente/emprendimientos")
public class EmprendimientoClienteController {

    private final EmprendimientoService emprendimientoService;

    public EmprendimientoClienteController(EmprendimientoService emprendimientoService) {

        this.emprendimientoService = emprendimientoService;
    }

    @Operation(
            summary = "Obtener todos los emprendimientos",
            description = "Devuelve una lista de todos los emprendimientos disponibles",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de emprendimientos obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getAllEmprendimientos(){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getAllEmprendimientos();

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }

    @Operation(
            summary = "Obtener emprendimientos por ID",
            description = "Devuelve un emprendimiento específico por su ID",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById (@PathVariable Long id, Usuario usuario){

        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);

        return emprendimiento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Obtener emprendimientos por nombre",
            description = "Devuelve una lista de emprendimientos que coinciden con el nombre proporcionado",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos con ese nombre"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/nombre/{nombreEmprendimiento}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByNombre(@PathVariable String nombreEmprendimiento){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByNombre(nombreEmprendimiento);

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }

    @Operation(
            summary = "Obtener emprendimientos por ciudad",
            description = "Devuelve una lista de emprendimientos que operan en la ciudad especificada",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos en esa ciudad"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByCiudad(@PathVariable String ciudad){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByCiudad(ciudad);

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }
}
