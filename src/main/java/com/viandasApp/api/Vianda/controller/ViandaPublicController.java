package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.service.ViandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/public/viandas")
@Tag(name = "Viandas - Público")
@RequiredArgsConstructor
public class ViandaPublicController {

    private final ViandaService viandasService;
    private final PagedResourcesAssembler<ViandaDTO> pagedResourcesAssembler;

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener viandas disponibles por emprendimiento",
            description = "Obtiene la lista paginada de viandas disponibles para el emprendimiento especificado por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viandas encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento o viandas no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<PagedModel<EntityModel<ViandaDTO>>> getViandasDisponiblesByEmprendimiento(
            @Valid @ModelAttribute FiltroViandaDTO filtro,
            @PathVariable Long idEmprendimiento,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ViandaDTO> page = viandasService.getViandasDisponiblesByEmprendimiento(filtro, idEmprendimiento, pageable);

        PagedModel<EntityModel<ViandaDTO>> pagedModel = pagedResourcesAssembler.toModel(page, vianda -> {
            // Links HATEOAS para cada vianda
            vianda.add(linkTo(methodOn(ViandaPublicController.class).getById(vianda.getId())).withSelfRel());
            return EntityModel.of(vianda);
        });

        return ResponseEntity.ok(pagedModel);
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
    public ResponseEntity<ViandaDTO> getById(@PathVariable Long id) {
        ViandaDTO vianda = viandasService.findViandaByIdPublic(id).get();
        vianda.add(linkTo(methodOn(ViandaPublicController.class).getById(id)).withSelfRel());
        return ResponseEntity.ok(vianda);
    }

    @Operation(
            summary = "Obtener categorías por emprendimiento",
            description = "Devuelve la lista de categorías únicas de viandas disponibles de un emprendimiento."
    )
    @GetMapping("/categorias/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<CategoriaVianda>> getCategoriasByEmprendimiento(@PathVariable Long idEmprendimiento) {
        List<CategoriaVianda> categorias = viandasService.getCategoriasByEmprendimiento(idEmprendimiento, null);
        return ResponseEntity.ok(categorias);
    }
}