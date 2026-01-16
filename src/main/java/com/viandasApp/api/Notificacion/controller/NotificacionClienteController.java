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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Notificaciones - Cliente")
@RequestMapping("/api/cliente/notificaciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENTE')")
public class NotificacionClienteController {
    private final NotificacionService notificacionService;

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener notificaciones (Filtro opcional)",
            description = "Devuelve notificaciones. Usa ?leida=false para ver solo las nuevas.",
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
    public ResponseEntity<?> getAllNotificacionesPropias(
            @RequestParam(required = false) Boolean leida
    ) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Pasamos el filtro al servicio
        List<NotificacionDTO> notificaciones = notificacionService.getAllByDestinatarioId(autenticado.getId(), leida);
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(
            summary = "Obtener notificaciones propias entre fechas",
            description = "Devuelve una lista de notificaciones del usuario autenticado entre dos fechas",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, fechas incorrectas"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/entre-fechas")
    public ResponseEntity<?> getAllPropiosByFechas(
            @RequestParam("desde") LocalDate desde,
            @RequestParam("hasta") LocalDate hasta) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NotificacionDTO> notificaciones = notificacionService.getAllByFechaEnviadoBetweenAndDestinatarioId(autenticado.getId(), desde, hasta);
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(
            summary = "Marcar notificación como leída",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificacionDTO> marcarComoLeida(@PathVariable Long id) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NotificacionDTO notificacion = notificacionService.marcarComoLeida(id, autenticado.getId());
        return ResponseEntity.ok(notificacion);
    }
}
