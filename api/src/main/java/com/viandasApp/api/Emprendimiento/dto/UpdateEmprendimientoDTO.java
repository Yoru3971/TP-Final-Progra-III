package com.viandasApp.api.Emprendimiento.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmprendimientoDTO {

    @Size(min=1, max=255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreEmprendimiento;

    @Size(min=1, max=255, message = "La ciudad debe tener entre {min} y {max} caracteres.")
    private String ciudad;

    @Size(max = 255, message = "La dirección puede contener como máximo {max} caracteres.")
    private String direccion;

    @Size(min=7, max=15, message = "El teléfono debe tener entre {min} y {max} dígitos.")
    @Pattern(regexp = "\\d+", message = "El teléfono debe contener solo números.")
    private String telefono;

    @Min(value = 1, message = "El ID del usuario debe ser mayor o igual a 1.")
    private Long idUsuario;

}
