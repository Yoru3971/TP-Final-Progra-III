package com.viandasApp.api.Auth.service;


import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.model.RefreshToken;
import com.viandasApp.api.Usuario.model.Usuario;

public interface RefreshTokenService {
    RefreshToken findByToken(String token);
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    UsuarioLogedResponseDTO procesarRefresh(String refreshTokenStr);
    void deleteByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
