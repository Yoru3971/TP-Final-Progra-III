package com.viandasApp.api.Usuario.controller.auth;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioLoginDTO;
import com.viandasApp.api.Usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(usuarioCreateDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDTO loginDTO) {

        Optional<UsuarioDTO> usuario = usuarioService.findByEmail(loginDTO.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(), loginDTO.getPassword()
                )
        );

        return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "usuarioId", usuario.get().getId(),
                "email", usuario.get().getEmail(),
                "rol", usuario.get().getRolUsuario()
        ));
    }
}