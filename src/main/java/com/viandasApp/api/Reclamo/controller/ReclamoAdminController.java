package com.viandasApp.api.Reclamo.controller;

import com.viandasApp.api.Reclamo.dto.ReclamoDTO;
import com.viandasApp.api.Reclamo.dto.ReclamoUpdateDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Reclamos - Admin")
@RequestMapping("/api/admin/reclamos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReclamoAdminController {

    private final ReclamoService reclamoService;
    private final PagedResourcesAssembler<ReclamoDTO> pagedResourcesAssembler;

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener todos los reclamos (Paginado)",
            description = "Obtiene lista paginada de reclamos, con filtros opcionales",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reclamos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ReclamoDTO>>> getAllReclamos(
            @RequestParam(required = false) EstadoReclamo estado,
            @RequestParam(required = false) String email,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @AuthenticationPrincipal Usuario usuario
    ) {
        Page<Reclamo> page = reclamoService.buscarReclamos(usuario, estado, email, pageable);

        Page<ReclamoDTO> dtoPage = page.map(ReclamoDTO::new);

        PagedModel<EntityModel<ReclamoDTO>> pagedModel = pagedResourcesAssembler.toModel(dtoPage, r -> {
            return EntityModel.of(r);
        });

        return ResponseEntity.ok(pagedModel);
    }

    //--------------------------Read (Por ID)--------------------------//
    @Operation(
            summary = "Obtener reclamo por ID",
            description = "Obtiene el detalle de un reclamo específico",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reclamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Reclamo no encontrado")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getReclamoPorId(@PathVariable Long id) {
        Optional<Reclamo> reclamo = reclamoService.obtenerReclamoPorId(id);
        return ResponseEntity.ok(reclamo.get());
    }

    //--------------------------Update (Cambiar Estado)--------------------------//
    @Operation(
            summary = "Actualizar estado de un reclamo",
            description = "Permite cambiar el estado de un reclamo (ej: marcar como RESUELTO)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Reclamo no encontrado")
    })
    @PutMapping("/id/{id}/estado")
    public ResponseEntity<?> updateEstadoReclamo(@PathVariable Long id, @RequestBody ReclamoUpdateDTO dto) {
        Reclamo actualizado = reclamoService.actualizarEstadoReclamo(id, dto.getNuevoEstado(), dto.getRespuestaAdmin());
        return ResponseEntity.ok(actualizado);
    }
}