package com.viandasApp.api.Auth.service;

import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.model.RefreshToken;
import com.viandasApp.api.Auth.repository.RefreshTokenRepository;
import com.viandasApp.api.Security.jwt.JwtUtil;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED, "Refresh token no encontrado. Iniciá sesión nuevamente."
                        )
                );
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Usuario no encontrado."
                        )
                );

        refreshTokenRepository.deleteByUsuario(usuario);

        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Refresh token expirado. Iniciá sesión nuevamente."
            );
        }
        return token;
    }

    @Override
    @Transactional
    public UsuarioLogedResponseDTO procesarRefresh(String refreshTokenStr) {
        RefreshToken refreshToken = findByToken(refreshTokenStr);

        verifyExpiration(refreshToken);

        Usuario usuario = refreshToken.getUsuario();

        String nuevoJwt = jwtUtil.generateToken(usuario.getEmail(), usuario.getRolUsuario().name());

        return new UsuarioLogedResponseDTO(usuario.getId(), nuevoJwt);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUsuario(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }
}