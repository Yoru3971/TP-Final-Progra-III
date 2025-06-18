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
public class UsuarioCreateDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreCompleto;

    @Email
    @NotBlank(message = "El email es obligatorio.")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$",
            message = "La contraseña debe tener entre 8 y 16 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El rol es obligatorio.")
    private RolUsuario rolUsuario;

    @NotBlank(message = "El telefono es obligatorio.")
    @Pattern(regexp = "\\d{10,15}", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String telefono;
}
