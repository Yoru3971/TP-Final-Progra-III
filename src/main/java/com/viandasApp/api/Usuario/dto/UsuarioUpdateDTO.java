package com.viandasApp.api.Usuario.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioUpdateDTO {
    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String nombreCompleto;

    @Email
    @NotBlank(message = "El email es obligatorio.")
    @Size(min = 1, max = 64, message = "El nombre debe tener entre {min} y {max} caracteres.")
    private String email;

    @NotBlank(message = "El telefono es obligatorio.")
    @Pattern(regexp = "\\d{8,15}", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String telefono;
}
