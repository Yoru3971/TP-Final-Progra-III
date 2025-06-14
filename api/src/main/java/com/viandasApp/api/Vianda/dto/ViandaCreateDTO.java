package com.viandasApp.api.Vianda.dto;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.User.model.Usuario;
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
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre debe contener [max] caracteres")
    private String nombreVianda;

    @NotNull(message = "La categoria no puede estar vacia")
    private CategoriaVianda categoria;

    @Size(max = 400, message = "La descripcion puede contener [max] caracteres")
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private Double precio;

    @NotNull
    private Boolean esVegano;

    @NotNull
    private Boolean esVegetariano;

    @NotNull
    private Boolean esSinTacc;

    @NotNull
    private Emprendimiento emprendimiento;
}
