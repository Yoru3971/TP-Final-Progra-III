package com.viandasApp.api.Notificacion.controller;

import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notificaciones - Dueño")
@RequestMapping("/api/dueno/notificaciones")
@PreAuthorize("hasRole('DUENO')")
public class NotificacionDuenoController {

    private final NotificacionService notificacionService;
    private final PagedResourcesAssembler<NotificacionDTO> pagedResourcesAssembler;

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener notificaciones paginadas y filtradas (opcional)",
            description = "Devuelve notificaciones con paginación y con filtros opcionales de fecha y lectura.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
      @ApiResponses(value = {
              @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida correctamente"),
              @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
              @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
              @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones"),
              @ApiResponse(responseCode = "500", description = "Error interno del servidor")
      })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<NotificacionDTO>>> getNotificaciones(
            @RequestParam(required = false) Boolean leida,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<NotificacionDTO> page = notificacionService.buscarNotificaciones(usuario.getId(), leida, desde, hasta, pageable);

        PagedModel<EntityModel<NotificacionDTO>> pagedModel = pagedResourcesAssembler.toModel(page, notificacion -> {
            notificacion.add(linkTo(methodOn(NotificacionDuenoController.class).marcarComoLeida(notificacion.getId())).withRel("marcar-leida"));
            return EntityModel.of(notificacion);
        });

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Cantidad de notificaciones no leídas",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/no-leidas/cantidad")
    public ResponseEntity<Map<String, Long>> getCantidadNoLeidas() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long cantidad = notificacionService.contarNoLeidas(usuario.getId());

        Map<String, Long> response = new HashMap<>();
        response.put("cantidad", cantidad);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Marcar notificación como leída",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificacionDTO> marcarComoLeida(@PathVariable Long id) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NotificacionDTO notificacion = notificacionService.marcarComoLeida(id, autenticado.getId());
        return ResponseEntity.ok(notificacion);
    }
}
