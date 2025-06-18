package com.viandasApp.api.Emprendimiento.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmprendimientoDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min=1, max=255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreEmprendimiento;

    @NotBlank(message = "La ciudad es obligatoria.")
    @Size(min=1, max=255, message = "La ciudad debe tener entre {min} y {max} caracteres.")
    private String ciudad;

    @Size(max = 255, message = "La dirección debe tener como máximo {max} caracteres.")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Size(min=7, max=15, message = "El teléfono debe tener entre {min} y {max} dígitos.")
    @Pattern(regexp = "\\d+", message = "El teléfono debe contener solo números.")
    private String telefono;

    @NotNull(message = "El ID del usuario es obligatorio.")
    @Min(value = 1, message = "El ID del usuario debe ser mayor o igual a 1.")
    private Long idUsuario;
}
