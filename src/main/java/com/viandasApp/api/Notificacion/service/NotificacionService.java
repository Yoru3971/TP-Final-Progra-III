package com.viandasApp.api.Notificacion.service;

import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;

import java.time.LocalDate;
import java.util.List;

public interface NotificacionService {
    List<NotificacionDTO> getAllByDestinatarioId(Long destinatarioId);
    List<NotificacionDTO> getAllByEmprendimientoId(Long emprendimientoId);
    List<NotificacionDTO> getAllByFechaEnviadoBetween(LocalDate start, LocalDate end);
    List<NotificacionDTO> getAllNotificaciones();
    List<NotificacionDTO> getAllByFechaEnviadoBetweenAndDestinatarioId(Long destinatarioId, LocalDate start, LocalDate end);

    NotificacionDTO createNotificacion(NotificacionCreateDTO notificacionCreateDTO);
    boolean deleteNotificacion(Long id);
}
