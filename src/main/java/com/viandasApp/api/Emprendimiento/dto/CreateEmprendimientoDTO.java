package com.viandasApp.api.Emprendimiento.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmprendimientoDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min=1, max=256, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreEmprendimiento;

    @NotNull(message = "La imagen del emprendimiento es obligatoria.")
    private MultipartFile image;

    @NotBlank(message = "La ciudad es obligatoria.")
    @Size(min=1, max=256, message = "La ciudad debe tener entre {min} y {max} caracteres.")
    private String ciudad;

    @Size(max = 256, message = "La dirección debe tener como máximo {max} caracteres.")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "\\d{6,15}", message = "El teléfono debe tener entre 6 y 15 dígitos.")
    private String telefono;

    @NotNull(message = "El ID del usuario es obligatorio.")
    @Min(value = 1, message = "El ID del usuario debe ser mayor o igual a 1.")
    private Long idUsuario;
}
