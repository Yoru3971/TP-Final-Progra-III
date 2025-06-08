package com.viandasApp.api.Pedido.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateDTO {
    @NotNull
    private Long idCliente;

    @NotNull
    private Long idEmprendimiento;

    @NotNull
    private List<ItemPedidoDTO> items;
}