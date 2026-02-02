package com.viandasApp.api.Auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Security.jwt.JwtUtil;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    public UsuarioLogedResponseDTO loginWithGoogle(String idTokenString) {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;

        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El token de Google es inválido o expiró."
            );
        }

        if (idToken == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El token de Google es inválido."
            );
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El email " + email + " no está registrado. Tenés que registrarte para poder iniciar sesión."
                ));

        if (!usuario.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Esta cuenta no está activada. Buscá en tu bandeja de entrada el correo con las instrucciones " +
                    "para activarla."
            );
        }

        String jwt = jwtUtil.generateToken(usuario.getEmail(), usuario.getRolUsuario().name());
        
        return new UsuarioLogedResponseDTO(usuario.getId(), jwt);
    }
}
