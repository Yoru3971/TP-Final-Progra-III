// PedidoUpdateViandasDTO.java
package com.viandasApp.api.Pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoUpdateViandasDTO {

    @NotNull(message = "La lista de viandas no puede ser nula")
    private List<ViandaCantidadDTO> viandas;

    @Data
    public static class ViandaCantidadDTO {
        @NotNull(message = "El ID de vianda no puede ser nulo")
        private Long viandaId;

        @NotNull
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;
    }
}