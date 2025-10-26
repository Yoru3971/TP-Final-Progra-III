package com.viandasApp.api.Usuario.controller.auth;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioLoginDTO;
import com.viandasApp.api.Usuario.security.JwtUtil;
import com.viandasApp.api.Usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Autenticación - Público")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.registerUsuario(usuarioCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @Operation(summary = "Iniciar sesión", description = "Autenticación con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDTO loginDTO) {
        ///Sque el Optional porque get() en un Optional sin verificar si está presente lanza NoSuchElementException.

        //1.1.1 Autenticacion (valida email+password usando UsuarioDetailsServiceImpl)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        //1.1.2 Obtengo el usuario (para agregar id,mail y rol en la respuesta mi rey)
        UsuarioDTO usuario = usuarioService.findByEmail(loginDTO.getEmail())
                .orElseThrow( () -> new RuntimeException("Usuario no encontrado"));

        //1.1.3 Genero el token con el/los rol/el
        String roleName = usuario.getRolUsuario().name();
        String token = jwtUtil.generateToken(usuario.getEmail(), roleName);

        //1.1.4 Aca te devuelvo el token y un poquito de data del usuario
        return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "token", token,
                "expiresInMs", jwtUtil.getExpirationMs(),
                "usuarioId", usuario.getId(),
                "email", usuario.getEmail(),
                "rol", usuario.getRolUsuario()
        ));
    }
}