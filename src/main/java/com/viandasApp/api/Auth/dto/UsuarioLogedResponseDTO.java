package com.viandasApp.api.Auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioLogedResponseDTO {
    private Long usuarioID;
    private String Token;
}
