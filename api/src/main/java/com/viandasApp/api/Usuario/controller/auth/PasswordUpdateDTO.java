package com.viandasApp.api.Usuario.controller.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateDTO {

    @NotBlank(message = "La contraseña actual es obligatoria.")
    @Size(min = 4, max = 16, message = "La contraseña debe tener [min, max] caracteres.")
        private String passwordActual;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 4, max = 16, message = "La contraseña debe tener [min, max] caracteres.")
        private String passwordNueva;
}
