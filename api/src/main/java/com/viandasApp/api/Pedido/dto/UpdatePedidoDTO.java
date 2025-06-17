package com.viandasApp.api.Pedido.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePedidoDTO {
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntrega;
}