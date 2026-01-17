package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoAdminDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Emprendimientos - Admin Panel")
@RequestMapping("/api/admin/emprendimientos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmprendimientoAdminController {

    private final EmprendimientoService emprendimientoService;
    private final PagedResourcesAssembler<EmprendimientoAdminDTO> pagedResourcesAssembler;

    //--------------------------Read All (Admin)--------------------------//
    @Operation(
            summary = "Obtener todos los emprendimientos (Audit)",
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
        Page<EmprendimientoAdminDTO> page = emprendimientoService.getAllEmprendimientosForAdmin(pageable);

        PagedModel<EntityModel<EmprendimientoAdminDTO>> pagedModel = pagedResourcesAssembler
                .toModel(page, dto -> EntityModel.of(dto));

        return ResponseEntity.ok(pagedModel);
    }

    //--------------------------Read One (Admin)--------------------------//
    @Operation(
            summary = "Obtener emprendimiento por ID (Admin)",
            description = "Permite a un administrador ver el detalle completo de cualquier emprendimiento, sin importar el dueño.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        // Reutilizamos el metodo del service que ya contiene la logica de excepcion para ADMIN
        EmprendimientoDTO emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario).get();

        // HATEOAS apuntando al controller de Admin
        emprendimiento.add(linkTo(methodOn(EmprendimientoAdminController.class).getEmprendimientoById(id, usuario)).withSelfRel());
        emprendimiento.add(linkTo(methodOn(EmprendimientoAdminController.class).updateEmprendimiento(id, null, usuario)).withRel("update"));
        emprendimiento.add(linkTo(methodOn(EmprendimientoAdminController.class).deleteEmprendimiento(id, usuario)).withRel("delete"));

        return ResponseEntity.ok(emprendimiento);
    }

    //--------------------------Update (Admin)--------------------------//
    @Operation(
            summary = "Actualizar un emprendimiento por ID (Admin)",
            description = "Permite a un administrador forzar la actualización de datos de cualquier emprendimiento.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO,
            @AuthenticationPrincipal Usuario usuario
    ) {
        Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO, usuario);

        if (emprendimientoActualizado.isPresent()) {
            emprendimientoActualizado.get().add(linkTo(methodOn(EmprendimientoAdminController.class).getEmprendimientoById(id, usuario)).withSelfRel());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento actualizado correctamente (ADMIN):", emprendimientoActualizado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete (Admin)--------------------------//
    @Operation(
            summary = "Eliminar un emprendimiento por ID (Admin)",
            description = "Permite a un administrador eliminar (o dar de baja lógica) cualquier emprendimiento.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere rol ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deleteEmprendimiento(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {

        Map<String, String> response = new HashMap<>();

        // El servicio ya soporta borrado por ADMIN
        emprendimientoService.deleteEmprendimiento(id, usuario);

        response.put("message", "Emprendimiento eliminado correctamente (ADMIN)");
        return ResponseEntity.ok(response);
    }
}