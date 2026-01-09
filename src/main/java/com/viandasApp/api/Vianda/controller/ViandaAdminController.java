package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.ViandaAdminDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Viandas - Admin Panel")
@RequestMapping("/api/admin/viandas")
@RequiredArgsConstructor
public class ViandaAdminController {

    private final ViandaService viandaService;
    private final PagedResourcesAssembler<ViandaAdminDTO> pagedResourcesAssembler;

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
        if (usuario.getRolUsuario() != RolUsuario.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN.");
        }

        Page<ViandaAdminDTO> page = viandaService.getAllViandasForAdmin(pageable);

        PagedModel<EntityModel<ViandaAdminDTO>> pagedModel = pagedResourcesAssembler.toModel(page, EntityModel::of);

        return ResponseEntity.ok(pagedModel);
    }
}
