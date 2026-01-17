package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Viandas - Admin Panel")
@RequestMapping("/api/admin/viandas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ViandaAdminController {

    private final ViandaService viandaService;
    private final PagedResourcesAssembler<ViandaAdminDTO> adminAssembler;
    private final PagedResourcesAssembler<ViandaDTO> viandaAssembler;

    //--------------------------Read All (Audit)--------------------------//
    @Operation(
            summary = "Obtener todas las viandas (Incluidas las borradas)",
            description = "Devuelve una lista completa para auditoría. Muestra datos reales incluso si tienen baja lógica.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viandas obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMIN)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ViandaAdminDTO>>> getAllViandasAdmin(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @AuthenticationPrincipal Usuario usuario
    ) {

        Page<ViandaAdminDTO> page = viandaService.getAllViandasForAdmin(pageable);

        PagedModel<EntityModel<ViandaAdminDTO>> pagedModel = adminAssembler.toModel(page, vianda -> {
            vianda.add(linkTo(methodOn(ViandaAdminController.class).findViandaById(vianda.getId(), null)).withSelfRel());
            return EntityModel.of(vianda);
        });

        return ResponseEntity.ok(pagedModel);
    }

    //--------------------------Create--------------------------//
    @Operation(summary = "Crear vianda (Admin)", description = "Permite a un admin crear una vianda en cualquier emprendimiento.", security = @SecurityRequirement(name = "bearer-jwt"))
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createVianda(
            @Valid @ModelAttribute ViandaCreateDTO viandaCreateDTO,
            @AuthenticationPrincipal Usuario usuario) {

        ViandaDTO nuevaVianda = viandaService.createVianda(viandaCreateDTO, usuario);

        nuevaVianda.add(linkTo(methodOn(ViandaAdminController.class).findViandaById(nuevaVianda.getId(), usuario)).withSelfRel());

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda creada correctamente (ADMIN)", nuevaVianda);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //--------------------------Read One--------------------------//
    @Operation(summary = "Obtener vianda por ID (Admin)", security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> findViandaById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        // El servicio ya soporta que entre un ADMIN
        ViandaDTO vianda = viandaService.findViandaById(id, usuario).get();

        vianda.add(linkTo(methodOn(ViandaAdminController.class).findViandaById(id, usuario)).withSelfRel());
        vianda.add(linkTo(methodOn(ViandaAdminController.class).updateVianda(id, null, usuario)).withRel("update"));
        vianda.add(linkTo(methodOn(ViandaAdminController.class).deleteVianda(id, usuario)).withRel("delete"));

        return ResponseEntity.ok(vianda);
    }

    //--------------------------Read List by Emprendimiento--------------------------//
    @Operation(summary = "Obtener viandas de un emprendimiento (Admin)", security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<PagedModel<EntityModel<ViandaDTO>>> getViandasByEmprendimiento(
            @PathVariable Long idEmprendimiento,
            @ModelAttribute FiltroViandaDTO filtroViandaDTO,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @AuthenticationPrincipal Usuario usuario) {

        Page<ViandaDTO> page = viandaService.getViandasByEmprendimiento(filtroViandaDTO, idEmprendimiento, usuario, true, pageable);

        PagedModel<EntityModel<ViandaDTO>> pagedModel = viandaAssembler.toModel(page, vianda -> {
            vianda.add(linkTo(methodOn(ViandaAdminController.class).findViandaById(vianda.getId(), usuario)).withSelfRel());
            return EntityModel.of(vianda);
        });

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Obtener categorías por emprendimiento (Admin)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/categorias/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<CategoriaVianda>> getCategoriasByEmprendimiento(
            @PathVariable Long idEmprendimiento,
            @AuthenticationPrincipal Usuario usuario) {
        List<CategoriaVianda> categorias = viandaService.getCategoriasByEmprendimiento(idEmprendimiento, usuario);
        return ResponseEntity.ok(categorias);
    }

    //--------------------------Update--------------------------//
    @Operation(summary = "Actualizar vianda (Admin)", security = @SecurityRequirement(name = "bearer-jwt"))
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateVianda(
            @PathVariable Long id,
            @Valid @RequestBody ViandaUpdateDTO dto,
            @AuthenticationPrincipal Usuario usuario) {

        Optional<ViandaDTO> viandaActualizada = viandaService.updateVianda(id, dto, usuario);

        if (viandaActualizada.isPresent()) {
            viandaActualizada.get().add(linkTo(methodOn(ViandaAdminController.class).findViandaById(id, usuario)).withSelfRel());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda actualizada correctamente (ADMIN)", viandaActualizada);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar imagen de vianda (Admin)", security = @SecurityRequirement(name = "bearer-jwt"))
    @PutMapping("/id/{id}/imagen")
    public ResponseEntity<ViandaDTO> updateImagenVianda(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal Usuario usuario) {

        ViandaDTO vianda = viandaService.updateImagenVianda(id, image, usuario);
        vianda.add(linkTo(methodOn(ViandaAdminController.class).findViandaById(id, usuario)).withSelfRel());

        return ResponseEntity.ok(vianda);
    }

    //--------------------------Delete--------------------------//
    @Operation(summary = "Eliminar vianda (Admin)", security = @SecurityRequirement(name = "bearer-jwt"))
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deleteVianda(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        viandaService.deleteVianda(id, usuario);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Vianda eliminada correctamente (ADMIN)");
        return ResponseEntity.ok(response);
    }
}