package com.viandasApp.api.Auth.repository;

import com.viandasApp.api.Auth.model.ConfirmacionToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmacionTokenRepository extends JpaRepository<ConfirmacionToken, Long> {
    Optional<ConfirmacionToken> findByToken(String token);

    //para limpiar automaticamente los tokens viejos
    @Transactional
    @Modifying
    @Query("DELETE FROM ConfirmacionToken c WHERE c.expiresAt < ?1")
    void deleteExpiredTokens(LocalDateTime now);
}
