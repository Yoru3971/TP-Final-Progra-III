package com.viandasApp.api.Pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class ViandaCantidadDTO {

    @NotNull(message = "El ID de la vianda es obligatorio")
    private Long viandaId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}