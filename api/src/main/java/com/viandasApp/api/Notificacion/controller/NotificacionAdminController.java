package com.viandasApp.api.Notificacion.controller;

import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/notificaciones")
@RequiredArgsConstructor
public class NotificacionAdminController {
    private final NotificacionService notificacionService;

    //--------------------------Create--------------------------//
    @PostMapping
    public ResponseEntity<NotificacionDTO> createNotificacion(@Valid @RequestBody NotificacionCreateDTO dto) {
        NotificacionDTO nueva = notificacionService.createNotificacion(dto);
        return ResponseEntity.ok(nueva);
    }

    //--------------------------Read--------------------------//
    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> getAllNotificaciones() {
        List<NotificacionDTO> notificaciones = notificacionService.getAllNotificaciones();
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/destinatario/{id}")
    public ResponseEntity<List<NotificacionDTO>> getByDestinatario(@PathVariable Long id) {
        List<NotificacionDTO> notificaciones = notificacionService.getAllByDestinatarioId(id);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/emprendimiento/{id}")
    public ResponseEntity<List<NotificacionDTO>> getByEmprendimiento(@PathVariable Long id) {
        List<NotificacionDTO> notificaciones = notificacionService.getAllByEmprendimientoId(id);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/entre-fechas")
    public ResponseEntity<List<NotificacionDTO>> getByFechas(
            @RequestParam("desde") LocalDate desde,
            @RequestParam("hasta") LocalDate hasta) {
        List<NotificacionDTO> notificaciones = notificacionService.getAllByFechaEnviadoBetween(desde, hasta);
        return ResponseEntity.ok(notificaciones);
    }

    //--------------------------Delete--------------------------//
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        notificacionService.deleteNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
