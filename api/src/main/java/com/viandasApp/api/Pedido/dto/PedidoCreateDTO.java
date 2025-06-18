package com.viandasApp.api.Pedido.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
public class PedidoCreateDTO {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntrega;

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @NotNull(message = "El ID del emprendimiento no puede ser nulo")
    private Long emprendimientoId;

    @NotEmpty(message = "La lista de viandas no puede estar vacía")
    @Valid
    private List<ViandaCantidadDTO> viandas;
}
