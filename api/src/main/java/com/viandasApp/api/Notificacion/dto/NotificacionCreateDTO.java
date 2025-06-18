package com.viandasApp.api.Notificacion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionCreateDTO {

    @NotNull(message = "El ID del destinatario no puede ser nulo")
    private Long destinatarioId;

    private Long emprendimientoId;

    @NotNull(message = "El mensaje no puede ser nulo")
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEnviado;
}
