package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Emprendimientos - Público")
@RequestMapping("/api/public/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoPublicController {
    private final EmprendimientoService emprendimientoService;
    private final PagedResourcesAssembler<EmprendimientoDTO> pagedResourcesAssembler;
 
    //--------------------------Read--------------------------//
     @Operation(
            summary = "Obtener todos los emprendimientos disponibles (con paginación)",
            description = "Devuelve una lista de todos los emprendimientos disponibles con enlaces HATEOAS"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de emprendimientos obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos disponibles"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })  
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<EmprendimientoDTO>>> getAllEmprendimientos(
            @PageableDefault(size = 10, page = 0) Pageable pageable
     ){
         Page<EmprendimientoDTO> emprendimientosPage = emprendimientoService.getAllEmprendimientosDisponibles(pageable);

         PagedModel<EntityModel<EmprendimientoDTO>> pagedModel = pagedResourcesAssembler.toModel(emprendimientosPage, emprendimiento -> {
             emprendimiento.add(linkTo(methodOn(EmprendimientoPublicController.class).getEmprendimientoById(emprendimiento.getId())).withSelfRel());
             return EntityModel.of(emprendimiento);
         });

         return ResponseEntity.ok(pagedModel);
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
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById (@PathVariable Long id){
        EmprendimientoDTO emprendimiento = emprendimientoService.getEmprendimientoByIdPublic(id).get();

        emprendimiento.add(linkTo(methodOn(EmprendimientoPublicController.class).getEmprendimientoById(id)).withSelfRel());

        return ResponseEntity.ok(emprendimiento);
    }

    @Operation(
            summary = "Obtener emprendimientos disponibles por nombre",
            description = "Devuelve una lista de emprendimientos disponibles que coinciden con el nombre proporcionado",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos disponibles con ese nombre"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/nombre/{nombreEmprendimiento}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByNombre(@PathVariable String nombreEmprendimiento){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosDisponiblesByNombre(nombreEmprendimiento);
        return ResponseEntity.ok(emprendimientos);
    }

    @Operation(
            summary = "Obtener emprendimientos disponibles por ciudad",
            description = "Devuelve una lista de emprendimientos que operan en la ciudad especificada",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos disponibles en esa ciudad"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByCiudad(@PathVariable String ciudad){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosDisponiblesByCiudad(ciudad);
        return ResponseEntity.ok(emprendimientos);
    }
}
