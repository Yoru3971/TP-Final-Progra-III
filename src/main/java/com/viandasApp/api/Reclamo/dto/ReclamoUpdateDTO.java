package com.viandasApp.api.Reclamo.dto;

import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReclamoUpdateDTO {
    @NotNull
    private EstadoReclamo nuevoEstado;

    private String respuestaAdmin;
}
