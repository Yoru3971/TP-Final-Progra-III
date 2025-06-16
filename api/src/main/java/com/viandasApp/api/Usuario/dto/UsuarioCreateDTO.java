package com.viandasApp.api.Usuario.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioCreateDTO {
    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 1, max = 255, message = "El nombre debe tener [min, max] caracteres.")
    private String nombreCompleto;

    @Email
    @NotBlank(message = "El email es obligatorio.")
    @Size(min = 1, max = 64, message = "El email debe tener [min, max] caracteres.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 4, max = 16, message = "La contraseña debe tener [min, max] caracteres.")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El rol es obligatorio.")
    private RolUsuario rolUsuario;
}
