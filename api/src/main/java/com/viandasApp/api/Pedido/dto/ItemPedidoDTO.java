package com.viandasApp.api.Pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    @NotNull
    private Long idVianda;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
