package com.viandasApp.api.Vianda.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FiltroViandaDTO {

    private Boolean esVegano;

    private Boolean esVegetariano;

    private Boolean esSinTacc;

    private String categoria;

    @DecimalMin(value = "0.0", message = "El precio minimo no puede ser menor a cero.")
    private Double precioMin;

    @DecimalMin(value = "0.0", message = "El precio minimo no puede ser menor a cero.")
    private Double precioMax;

    private String nombreVianda;

    @AssertTrue(message = "El precio máximo debe ser mayor o igual al precio mínimo")
    private boolean isPrecioValido() {
        if (precioMin == null || precioMax == null) return true;
        return precioMax >= precioMin;
    }
}


