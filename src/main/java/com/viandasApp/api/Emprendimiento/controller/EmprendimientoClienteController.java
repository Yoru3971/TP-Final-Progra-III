package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Emprendimientos - Cliente")
@RequestMapping("/api/cliente/emprendimientos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENTE')")
public class EmprendimientoClienteController {

    private final EmprendimientoService emprendimientoService;
    private final PagedResourcesAssembler<EmprendimientoDTO> pagedResourcesAssembler;
    
    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener todos los emprendimientos disponibles (con paginación y filtrado)",
            description = "Devuelve una lista de todos los emprendimientos disponibles con enlaces HATEOAS",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        Page<Emprendimiento> page = emprendimientoService.buscarEmprendimientos(null, null, null, null, false, pageable);

        Page<EmprendimientoDTO> dtoPage = page.map(EmprendimientoDTO::new);

        PagedModel<EntityModel<EmprendimientoDTO>> pagedModel = pagedResourcesAssembler.toModel(dtoPage, emprendimiento -> {
            emprendimiento.add(linkTo(methodOn(EmprendimientoClienteController.class).getEmprendimientoById(emprendimiento.getId())).withSelfRel());
            return EntityModel.of(emprendimiento);
        });

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Obtener emprendimientos disponibles por ciudad (con paginación)",
            description = "Devuelve una lista de emprendimientos que operan en la ciudad especificada con enlaces HATEOAS",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos disponibles en esa ciudad"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<PagedModel<EntityModel<EmprendimientoDTO>>> getEmprendimientosByCiudad(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ){
        Page<Emprendimiento> page = emprendimientoService.buscarEmprendimientos(null, ciudad, nombre, null, false, pageable);

        PagedModel<EntityModel<EmprendimientoDTO>> pagedModel = pagedResourcesAssembler.toModel(page.map(EmprendimientoDTO::new), e -> {
            e.add(linkTo(methodOn(EmprendimientoClienteController.class).getEmprendimientoById(e.getId())).withSelfRel());
            return EntityModel.of(e);
        });
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Obtener emprendimiento por ID",
            description = "Devuelve un emprendimiento específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById (@PathVariable Long id){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmprendimientoDTO emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario).get();

        emprendimiento.add(linkTo(methodOn(EmprendimientoClienteController.class).getEmprendimientoById(id)).withSelfRel());

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

}
