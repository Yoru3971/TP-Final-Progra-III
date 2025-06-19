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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor

@Tag(name = "Notificaciones - Dueño", description = "Controlador para gestionar notificaciones del dueño")
@RequestMapping("/api/dueno/notificaciones")
public class NotificacionDuenoController {
    private final NotificacionService notificacionService;

    //--------------------------Read--------------------------//
    @Operation(
              summary = "Obtener todas las notificaciones propias",
              description = "Devuelve una lista de todas las notificaciones del usuario autenticado",
              security = @SecurityRequirement(name = "basicAuth")
      )
      @ApiResponses(value = {
              @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida correctamente"),
              @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
              @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
              @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones"),
              @ApiResponse(responseCode = "500", description = "Error interno del servidor")
      })
    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> getAllNotificacionesPropias() {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NotificacionDTO> notificaciones = notificacionService.getAllByDestinatarioId(autenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(notificaciones);
    }

    @Operation(
            summary = "Obtener notificaciones propias entre fechas",
            description = "Devuelve una lista de notificaciones del usuario autenticado entre dos fechas",
            security = @SecurityRequirement(name = "basicAuth")
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
    public ResponseEntity<List<NotificacionDTO>> getAllPropiosByFechas(
            @RequestParam("desde") LocalDate desde,
            @RequestParam("hasta") LocalDate hasta) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NotificacionDTO> notificaciones = notificacionService.getAllByFechaEnviadoBetweenAndDestinatarioId(autenticado.getId(), desde, hasta);
        return ResponseEntity.ok(notificaciones);
    }
}
