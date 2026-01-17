package com.viandasApp.api.Auth.service;

import com.viandasApp.api.Auth.dto.PasswordResetChangeDTO;
import com.viandasApp.api.Auth.model.PasswordResetToken;
import com.viandasApp.api.Auth.repository.PasswordTokenRepository;
import com.viandasApp.api.ServiceGenerales.EmailService;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService{

    private final UsuarioRepository usuarioRepository;
    private final PasswordTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createResetTokenForUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {
            String token = UUID.randomUUID().toString();

            PasswordResetToken myToken = tokenRepository.findByUser(usuario).orElse(null);

            if (myToken != null) {
                myToken.setToken(token);
                myToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 15)); //15min
                tokenRepository.save(myToken);
            } else {
                myToken = new PasswordResetToken(token, usuario);
                tokenRepository.save(myToken);
            }

            emailService.sendRecoveryEmail(usuario.getEmail(), usuario.getNombreCompleto(), token);
        }
    }

    @Override
    @Transactional
    public void changeUserPassword(PasswordResetChangeDTO passwordDto) {
        PasswordResetToken passToken = tokenRepository.findByToken(passwordDto.getToken());

        if (passToken == null) {
            throw new RuntimeException("Token inválido.");
        }

        if (passToken.getExpiryDate().before(new Date())) {
            tokenRepository.delete(passToken);
            throw new RuntimeException("El token ha expirado.");
        }

        Usuario usuario = passToken.getUser();
        usuario.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        usuarioRepository.save(usuario);

        tokenRepository.delete(passToken);
    }
}
