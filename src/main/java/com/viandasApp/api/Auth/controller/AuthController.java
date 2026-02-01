package com.viandasApp.api.Auth.controller;

import com.viandasApp.api.Auth.dto.GoogleTokenDTO;
import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.Auth.model.RefreshToken;
import com.viandasApp.api.Auth.service.AuthService;
import com.viandasApp.api.Auth.service.GoogleAuthService;
import com.viandasApp.api.Auth.service.RefreshTokenService;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Auth.dto.UsuarioLoginDTO;
import com.viandasApp.api.Utils.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Autenticación - Público")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    //--------------------------Register--------------------------//
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

    //--------------------------Login--------------------------//
    @Operation(summary = "Iniciar sesión", description = "Autenticación con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDTO usuarioLoginDTO) {
        UsuarioLogedResponseDTO usuarioResponse = authService.loginUsuario(usuarioLoginDTO);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuarioResponse.getUsuarioID());

        ResponseCookie cookie = cookieUtil.crearCookieRefresh(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(usuarioResponse);
    }

    //--------------------------Login con GMAIL--------------------------//
    @Operation(summary = "Login con Google", description = "Verifica token de Google y loguea si el usuario existe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve JWT"),
            @ApiResponse(responseCode = "403", description = "Usuario no registrado (Debe registrarse)"),
            @ApiResponse(responseCode = "401", description = "Cuenta deshabilitada"),
            @ApiResponse(responseCode = "400", description = "Token de Google inválido")
    })
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleTokenDTO googleTokenDto) {
        UsuarioLogedResponseDTO usuarioResponse = googleAuthService.loginWithGoogle(googleTokenDto.getToken());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuarioResponse.getUsuarioID());

        ResponseCookie cookie = cookieUtil.crearCookieRefresh(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(usuarioResponse);
    }

    //--------------------------Refresh Token--------------------------//
    @Operation(summary = "Refrescar Token", description = "Usa la cookie HttpOnly para obtener un nuevo JWT sin loguearse de nuevo")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refrescarToken(
            @CookieValue(name = "refreshToken", defaultValue = "") String refreshTokenStr) {

        if (refreshTokenStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se encontró Refresh Token en las cookies");
        }

        UsuarioLogedResponseDTO nuevoTokenResponse = refreshTokenService.procesarRefresh(refreshTokenStr);

        return ResponseEntity.ok(nuevoTokenResponse);
    }

    //--------------------------Logout--------------------------//
    @Operation(summary = "Cerrar sesión", description = "Borra la sesión del dispositivo (Cookie y BD)")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUsuario(
            @CookieValue(name = "refreshToken", defaultValue = "") String refreshTokenStr) {

        if (!refreshTokenStr.isEmpty()) {
            refreshTokenService.deleteByToken(refreshTokenStr);
        }

        ResponseCookie cookieLimpiada = cookieUtil.limpiarCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieLimpiada.toString())
                .body("Sesión cerrada exitosamente en este dispositivo");
    }

    //--------------------------Utils--------------------------//
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