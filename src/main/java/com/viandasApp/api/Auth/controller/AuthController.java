package com.viandasApp.api.Auth.controller;

import com.viandasApp.api.Auth.dto.GoogleTokenDto;
import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.Auth.service.AuthService;
import com.viandasApp.api.Auth.service.AuthServiceImpl;
import com.viandasApp.api.Auth.service.GoogleAuthService;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Auth.dto.UsuarioLoginDTO;
import com.viandasApp.api.Usuario.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Autenticación - Público")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioRegisterDTO usuarioRegisterDTO) {
        UsuarioDTO nuevoUsuario = authService.registerUsuario(usuarioRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    //=== LOGIN ===
    //--- CREDENCIALES ---
    @Operation(summary = "Iniciar sesión", description = "Autenticación con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDTO usuarioLoginDTO) {
        UsuarioLogedResponseDTO usuarioLogeado = authService.loginUsuario(usuarioLoginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioLogeado);
    }

    //--- GMAIL ---
    @Operation(summary = "Login con Google", description = "Verifica token de Google y loguea si el usuario existe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve JWT"),
            @ApiResponse(responseCode = "403", description = "Usuario no registrado (Debe registrarse)"),
            @ApiResponse(responseCode = "401", description = "Cuenta deshabilitada"),
            @ApiResponse(responseCode = "400", description = "Token de Google inválido")
    })
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleTokenDto googleTokenDto) {
        Map<String, Object> respuesta = googleAuthService.loginWithGoogle(googleTokenDto.getToken());
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Confirmar cuenta", description = "Valida el token enviado por email y habilita al usuario")
    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        String result = authService.confirmToken(token);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Reenviar token", description = "Genera un nuevo token de validación si la cuenta no está activa")
    @PostMapping("/resend-token")
    public ResponseEntity<String> resendToken(@RequestParam("email") String email) {
        String result = authService.resendToken(email);
        return ResponseEntity.ok(result);
    }
}