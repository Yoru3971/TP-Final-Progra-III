package com.viandasApp.api.Notificacion.controller;

import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Notificaciones - Admin")
@RequestMapping("/api/admin/notificaciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NotificacionAdminController {

    private final NotificacionService notificacionService;
    private final PagedResourcesAssembler<NotificacionDTO> pagedResourcesAssembler;

    //--------------------------Create--------------------------//    
   @Operation(
            summary = "Crear una nueva notificación",
            description = "Permite al administrador crear una nueva notificación",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<NotificacionDTO> createNotificacion(@Valid @RequestBody NotificacionCreateDTO dto) {
        NotificacionDTO nueva = notificacionService.createNotificacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    //--------------------------Read--------------------------//

    @Operation(
            summary = "Obtener notificación por ID de destinatario",
            description = "Devuelve las notificaciones de un usuario específico",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/destinatario/{id}")
    public ResponseEntity<PagedModel<EntityModel<NotificacionDTO>>> getByDestinatario(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {

        var page = notificacionService.buscarNotificaciones(id, null, null, null, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, EntityModel::of));
    }

    //--------------------------Delete--------------------------//
    @Operation(
            summary = "Eliminar una notificación",
            description = "Permite al administrador eliminar una notificación existente",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        notificacionService.deleteNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
