package com.viandasApp.api.Pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
public class PedidoCreateDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long cliente_id;

    @NotEmpty(message = "La lista de viandas no puede estar vacía")
    @Valid
    private List<ViandaCantidadDTO> viandas;
}
