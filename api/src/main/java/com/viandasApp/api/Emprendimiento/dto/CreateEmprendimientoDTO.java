package com.viandasApp.api.Emprendimiento.dto;


import com.viandasApp.api.User.model.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmprendimientoDTO {

    @NotBlank(message = "El nombre del emprendimiento no puede estar vacío")
    @Size(min=3, max=100, message = "El nombre debe tener entre [min] y [max] caracteres")
    private String nombreEmprendimiento;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Size(min=3, max=100, message = "La ciudad debe tener entre [min] y [max] caracteres")
    private String ciudad;

    @Size(max = 100, message = "La dirección puede contener como máximo [max] caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Size(min=7, max=15, message = "El teléfono debe tener entre [min] y [max] dígitos")
    @Pattern(regexp = "\\d+", message = "El teléfono debe contener solo números")
    private String telefono;

    //  la validación del rol debería ir en el servicio (y acá usar solo el id?)
    @NotNull
    private User usuario;


}
