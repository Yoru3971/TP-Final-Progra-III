package com.viandasApp.api.Pedido.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePedidoDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntrega;
}