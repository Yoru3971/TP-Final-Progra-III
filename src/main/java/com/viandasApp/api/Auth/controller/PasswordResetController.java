package com.viandasApp.api.Auth.controller;

import com.viandasApp.api.Auth.dto.PasswordResetChangeDTO;
import com.viandasApp.api.Auth.dto.PasswordResetRequestDTO;
import com.viandasApp.api.Auth.service.PasswordResetServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Autenticación - Recuperación de Contraseña")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetServiceImpl passwordResetService;

    @Operation(summary = "Solicitar recuperación", description = "Envía un email con un token para restablecer la contraseña si el correo existe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud procesada correctamente (siempre devuelve OK por seguridad)"),
            @ApiResponse(responseCode = "400", description = "Formato de email inválido")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        passwordResetService.createResetTokenForUser(request.getEmail());
        return ResponseEntity.ok("Si el correo está registrado, recibirás instrucciones.");
    }

    @Operation(summary = "Cambiar contraseña", description = "Permite cambiar la contraseña utilizando el token recibido por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado o contraseña no cumple requisitos")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetChangeDTO request) {
        try {
            passwordResetService.changeUserPassword(request);
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
