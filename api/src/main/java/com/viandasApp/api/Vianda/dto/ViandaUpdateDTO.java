package com.viandasApp.api.Vianda.dto;

import com.viandasApp.api.Vianda.model.CategoriaVianda;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViandaUpdateDTO {
    @Size(max = 100, message = "El nombre debe contener max 100 caracteres")
    private String nombreVianda;

    private CategoriaVianda categoria;

    @Size(max = 400, message = "La descripcion puede contener max 400 caracteres")
    private String descripcion;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private Double precio;

    private Boolean esVegano;

    private Boolean esVegetariano;

    private Boolean esSinTacc;
}
