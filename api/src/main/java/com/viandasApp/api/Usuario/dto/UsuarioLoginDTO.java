package com.viandasApp.api.Usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioLoginDTO {
    private String email;
    private String password;
}
