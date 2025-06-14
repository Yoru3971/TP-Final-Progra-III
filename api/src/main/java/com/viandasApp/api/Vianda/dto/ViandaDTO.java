package com.viandasApp.api.Vianda.dto;


import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViandaDTO {
    private Long id;

    private String nombreVianda;

    private CategoriaVianda categoria;

    private String descripcion;

    private Double precio;

    private Boolean esVegano;

    private Boolean esVegetariano;

    private Boolean esSinTacc;

    private Emprendimiento emprendimiento;
}
