package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoAdminDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.RolUsuario;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Emprendimientos - Admin Panel")
@RequestMapping("/api/admin/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoAdminController {

    private final EmprendimientoService emprendimientoService;
    private final PagedResourcesAssembler<EmprendimientoAdminDTO> pagedResourcesAssembler;

    //--------------------------Read (Admin)--------------------------//
    @Operation(
            summary = "Obtener todos los emprendimientos (Incluidos los borrados)",
            description = "Devuelve una lista completa para auditoría. Muestra datos reales incluso si tienen baja lógica.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de emprendimientos obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMIN)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<EmprendimientoAdminDTO>>> getAllEmprendimientosAdmin(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @AuthenticationPrincipal Usuario usuario
    ) {
        if (usuario.getRolUsuario() != RolUsuario.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN.");
        }

        Page<EmprendimientoAdminDTO> page = emprendimientoService.getAllEmprendimientosForAdmin(pageable);

        PagedModel<EntityModel<EmprendimientoAdminDTO>> pagedModel = pagedResourcesAssembler
                .toModel(page, dto -> {
                    return EntityModel.of(dto);
        });

        return ResponseEntity.ok(pagedModel);
    }
}
