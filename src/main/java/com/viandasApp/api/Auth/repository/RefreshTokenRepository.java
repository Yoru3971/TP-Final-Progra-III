package com.viandasApp.api.Auth.repository;

import com.viandasApp.api.Auth.model.RefreshToken;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    // Logout normal
    @Modifying
    void deleteByToken(String token);

    // Cerrar sesión en todos los dispositivos
    @Modifying
    void deleteByUsuario(Usuario usuario);
}
