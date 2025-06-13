package com.viandasApp.api.Pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViandaCantidadDTO {
    @NotNull

    private Long vianda_id;
    @NotNull
    @Min(1)
    private Integer cantidad;
}
