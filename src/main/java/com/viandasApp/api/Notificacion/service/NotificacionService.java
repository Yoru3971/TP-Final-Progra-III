package com.viandasApp.api.Notificacion.service;

import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface NotificacionService {

    Page<NotificacionDTO> buscarNotificaciones(Long destinatarioId, Boolean leida, LocalDate desde, LocalDate hasta, Pageable pageable);
    long contarNoLeidas(Long destinatarioId);

    NotificacionDTO createNotificacion(NotificacionCreateDTO notificacionCreateDTO);
    boolean deleteNotificacion(Long id);
    NotificacionDTO marcarComoLeida(Long notificacionId, Long destinatarioId);
}
