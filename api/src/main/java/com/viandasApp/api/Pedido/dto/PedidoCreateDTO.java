package com.viandasApp.api.Pedido.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateDTO {
    private Long cliente_id; //Podria omitirse si se toma del usuario autenticado, checkear
    private List<ViandaCantidadDTO> viandas;
}