package com.viandasApp.api.Emprendimiento.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmprendimientoDTO {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min=1, max=256, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreEmprendimiento;

    @NotBlank(message = "La ciudad es obligatoria.")
    @Size(min=1, max=256, message = "La ciudad debe tener entre {min} y {max} caracteres.")
    private String ciudad;

    @Size(max = 256, message = "La dirección puede contener como máximo {max} caracteres.")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "\\d{6,15}", message = "El teléfono debe tener entre 6 y 15 dígitos.")
    private String telefono;

    private Boolean estaDisponible;

    @Min(value = 1, message = "El ID del usuario debe ser mayor o igual a 1.")
    private Long idUsuario;

}
