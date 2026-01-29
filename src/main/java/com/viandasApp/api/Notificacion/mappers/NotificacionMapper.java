package com.viandasApp.api.Notificacion.mappers;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.model.Notificacion;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {

    public Notificacion DTOToEntity(NotificacionCreateDTO dto, Usuario destinatario, Emprendimiento emprendimiento) {

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setEmprendimiento(emprendimiento);
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setFechaEnviado(dto.getFechaEnviado());
        notificacion.setLeida(false);

        return notificacion;
    }

}
