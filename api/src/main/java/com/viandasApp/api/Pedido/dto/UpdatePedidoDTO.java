package com.viandasApp.api.Pedido.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePedidoDTO {

    @NotNull(message = "El estado no puede ser nulo")
    private EstadoPedido estado;

    @NotNull(message = "La fecha no puede ser nula")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
}