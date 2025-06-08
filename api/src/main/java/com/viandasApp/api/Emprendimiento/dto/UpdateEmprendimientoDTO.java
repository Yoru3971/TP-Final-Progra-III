package com.viandasApp.api.Emprendimiento.dto;


import com.viandasApp.api.User.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmprendimientoDTO {

    @Size(min=3, max=100, message = "El nombre debe tener entre [min] y [max] caracteres")
    private String nombreEmprendimiento;

    @Size(min=3, max=100, message = "La ciudad debe tener entre [min] y [max] caracteres")
    private String ciudad;

    @Size(max = 100, message = "La dirección puede contener como máximo [max] caracteres")
    private String direccion;

    @Size(min=7, max=15, message = "El teléfono debe tener entre [min] y [max] dígitos")
    @Pattern(regexp = "\\d+", message = "El teléfono debe contener solo números")
    private String telefono;

    @NotNull
    private User usuario;

}
