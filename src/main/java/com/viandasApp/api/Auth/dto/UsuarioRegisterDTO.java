package com.viandasApp.api.Auth.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioRegisterDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 1, max = 256, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreCompleto;

    @Email
    @NotBlank(message = "El email es obligatorio.")
    @Size(min = 1, max = 254, message = "El nombre debe tener entre {min} y {max} caracteres.")
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
    @Pattern(regexp = "\\d{6,15}", message = "El teléfono debe tener entre 6 y 15 dígitos.")
    private String telefono;
}
