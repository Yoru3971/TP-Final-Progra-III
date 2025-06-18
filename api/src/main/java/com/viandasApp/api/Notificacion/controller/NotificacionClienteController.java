package com.viandasApp.api.Notificacion.controller;

import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cliente/notificaciones")
@RequiredArgsConstructor
public class NotificacionClienteController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> getAllNotificacionesPropias() {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<NotificacionDTO> notificaciones = notificacionService.getAllByDestinatarioId(autenticado.getId());
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/entre-fechas")
    public ResponseEntity<List<NotificacionDTO>> getAllPropiosByFechas(
            @RequestParam("desde") LocalDate desde,
            @RequestParam("hasta") LocalDate hasta) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<NotificacionDTO> notificaciones = notificacionService.getAllByFechaEnviadoBetweenAndDestinatarioId(autenticado.getId(), desde, hasta);
        return ResponseEntity.ok(notificaciones);
    }
}
