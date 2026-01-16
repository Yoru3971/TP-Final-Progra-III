package com.viandasApp.api.ServiceGenerales;

import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Auth.repository.ConfirmacionTokenRepository; // Asegúrate que el nombre coincida (Confirmacion vs Confirmation)
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CleanupService {

    private final UsuarioRepository usuarioRepository;
    private final ConfirmacionTokenRepository confirmacionTokenRepository;

    // Se ejecuta todos los días a las 4:00 AM
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void removeUnverifiedUsers() {

        LocalDateTime limite = LocalDateTime.now().minusHours(24);

        usuarioRepository.deleteByEnabledFalseAndCreatedAtBefore(limite);

        confirmacionTokenRepository.deleteExpiredTokens(LocalDateTime.now());

        System.out.println("Limpieza automática: Se eliminaron usuarios no verificados creados antes de " + limite);
    }
}
