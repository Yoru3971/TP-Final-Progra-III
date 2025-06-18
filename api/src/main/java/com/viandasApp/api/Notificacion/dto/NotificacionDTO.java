package com.viandasApp.api.Notificacion.dto;

import com.viandasApp.api.Notificacion.model.Notificacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {
    private Long id;
    private String mensaje;
    private Long destinatarioId;
    private Long emprendimientoId;
    private LocalDate fechaEnviado;

    public NotificacionDTO(Notificacion notificacion) {
        this.id = notificacion.getId();
        this.mensaje = notificacion.getMensaje();
        this.destinatarioId = notificacion.getDestinatario().getId();
        this.emprendimientoId = notificacion.getEmprendimiento().getId();
        this.fechaEnviado = notificacion.getFechaEnviado();
    }
}
