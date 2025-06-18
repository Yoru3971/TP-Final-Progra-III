package com.viandasApp.api.Usuario.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioUpdateRolDTO {

    @Size(min = 1, max = 255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreCompleto;

    @Email
    @Size(min = 1, max = 64, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El rol es obligatorio.")
    private RolUsuario rolUsuario;

    @Pattern(regexp = "\\d{10,15}", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String telefono;
}
