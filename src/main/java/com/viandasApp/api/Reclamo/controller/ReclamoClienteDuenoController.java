package com.viandasApp.api.Reclamo.controller;

import com.viandasApp.api.Reclamo.dto.ReclamoDTO;
import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import com.viandasApp.api.Reclamo.service.ReclamoService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Reclamos - Usuario logueado")
@RequestMapping("/api/logged/reclamos") // Aplica tanto para Cliente como Dueño
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENTE', 'DUENO')")
public class ReclamoClienteDuenoController {

    private final ReclamoService reclamoService;
    private final PagedResourcesAssembler<ReclamoDTO> pagedResourcesAssembler;

    //--------------------------Read (Mis Reclamos)--------------------------//
    @Operation(
            summary = "Obtener mis reclamos (Paginado)",
            description = "Permite al usuario logueado ver el historial de sus reclamos realizados",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reclamos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ReclamoDTO>>> getMisReclamos(
            @RequestParam(required = false) EstadoReclamo estado,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @AuthenticationPrincipal Usuario usuario
    ) {
        // El usuario solo puede ver sus reclamos, pasamos null en 'emailFiltro' porque el servicio lo fuerza
        Page<Reclamo> page = reclamoService.buscarReclamos(usuario, estado, null, pageable);

        Page<ReclamoDTO> dtoPage = page.map(ReclamoDTO::new);

        PagedModel<EntityModel<ReclamoDTO>> pagedModel = pagedResourcesAssembler.toModel(dtoPage, r -> EntityModel.of(r));

        return ResponseEntity.ok(pagedModel);
    }

}
