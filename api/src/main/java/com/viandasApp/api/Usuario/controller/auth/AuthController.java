package com.viandasApp.api.Usuario.controller.auth;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioLoginDTO;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(procesarErrores(result));
        }
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(usuarioCreateDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDTO loginDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(procesarErrores(result));
        }
        try {
            var usuarios = usuarioService.findByEmail(loginDTO.getEmail());
            if (usuarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            UsuarioDTO usuario = usuarios.get(0);

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(), loginDTO.getPassword()
                    )
            );

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Login exitoso",
                    "usuarioId", usuario.getId(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRolUsuario().name()
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    private static Map<String, String> procesarErrores(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(
                error -> errores.put(error.getField(), error.getDefaultMessage())
        );
        return errores;
    }
}