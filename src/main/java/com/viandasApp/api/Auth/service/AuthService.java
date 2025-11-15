package com.viandasApp.api.Auth.service;

import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.dto.UsuarioLoginDTO;
import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    UsuarioDTO registerUsuario(UsuarioRegisterDTO usuarioRegisterDTO);
    UsuarioLogedResponseDTO loginUsuario(UsuarioLoginDTO usuarioLoginDTO);
}
