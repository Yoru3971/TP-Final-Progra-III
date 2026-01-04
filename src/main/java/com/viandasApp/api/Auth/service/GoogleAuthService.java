package com.viandasApp.api.Auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Usuario.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    public Map<String, Object> loginWithGoogle(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;

        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token de Google es inválido o ha expirado.");
        }

        if (idToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token de Google es inválido.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, // 403 Forbidden
                        "El correo " + email + " no está registrado en el sistema. Por favor regístrese primero."
                ));


        if (!usuario.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Su cuenta existe pero no está habilitada. Revise su correo para confirmarla."
            );
        }

        //generamos el JWT
        String jwt = jwtUtil.generateToken(usuario.getEmail(), usuario.getRolUsuario().name());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("token", jwt);
        respuesta.put("usuarioID", usuario.getId());
        respuesta.put("email", usuario.getEmail());
        respuesta.put("role", usuario.getRolUsuario());

        return respuesta;
    }
}
