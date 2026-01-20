package com.viandasApp.api.Auth.repository;

import com.viandasApp.api.Auth.model.PasswordResetToken;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    Optional<PasswordResetToken> findByUser(Usuario user);
}
