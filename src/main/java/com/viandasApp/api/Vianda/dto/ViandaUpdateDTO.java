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
    @Size(min = 1, max = 255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreVianda;

    private CategoriaVianda categoria;

    @Size(max = 250, message = "La descripcion debe tener como máximo {max} caracteres.")
    private String descripcion;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo.")
    private Double precio;

    private Boolean esVegano;

    private Boolean esVegetariano;

    private Boolean esSinTacc;

    private Boolean estaDisponible;
}
