package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(
            @Valid @RequestBody UsuarioCreateDTO userDto,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        final UsuarioDTO createdUser = service.createUsuario(userDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> readUsuarios() {
        final List<UsuarioDTO> users = service.readUsuarios();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(
            @PathVariable Long id,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        final Optional<UsuarioDTO> user = service.findById(id);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombreCompleto}")
    public ResponseEntity<?> findByNombreCompleto(
            @PathVariable String nombreCompleto,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        final Optional<UsuarioDTO> user = service.findByNombreCompleto(nombreCompleto);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(
            @PathVariable String email,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        final Optional<UsuarioDTO> user = service.findByEmail(email);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{rolUsuario}")
    public ResponseEntity<?> findByRolUsuario(
            @PathVariable RolUsuario rolUsuario,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        final Optional<UsuarioDTO> user = service.findByRolUsuario(rolUsuario);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO userDto,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        final Optional<UsuarioDTO> updatedUser = service.updateUsuario(id, userDto);

        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long id,
            BindingResult result
    ) {
        final var errores = procesarErrores(result);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(errores);
        }

        if (service.deleteUsuario(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private static Map<String, String> procesarErrores(BindingResult result) {
        Map<String, String> errores = new HashMap<>();

        if (result.hasErrors()) {
            result.getFieldErrors().forEach(
                    error -> errores.put(error.getField(), error.getDefaultMessage())
            );
        }

        return errores;
    }
}
