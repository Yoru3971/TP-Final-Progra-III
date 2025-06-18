package com.viandasApp.api.Vianda.dto;

import com.viandasApp.api.Vianda.model.CategoriaVianda;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViandaCreateDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreVianda;

    @NotNull(message = "La categoría es obligatoria.")
    private CategoriaVianda categoria;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(max = 400, message = "La descripcion debe tener como máximo {max} caracteres.")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo.")
    private Double precio;

    @NotNull(message = "Indique si es vegano.")
    private Boolean esVegano;

    @NotNull(message = "Indique si es vegetariano.")
    private Boolean esVegetariano;

    @NotNull(message = "Indique si es sin TACC.")
    private Boolean esSinTacc;

    @NotNull(message = "El emprendimiento es obligatorio.")
    private Long emprendimientoId;
}
