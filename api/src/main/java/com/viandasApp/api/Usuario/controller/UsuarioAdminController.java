package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.controller.auth.AdminPasswordUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UsuarioAdminController {
    private final UsuarioService service;

    public UsuarioAdminController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(procesarErrores(result));
        }
        UsuarioDTO nuevoUsuario = service.createUsuario(usuarioCreateDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping
    public ResponseEntity<?> readUsuarios() {
        List<UsuarioDTO> usuarios = service.readUsuarios();

        if (usuarios.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No hay usuarios registrados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/me")
    public ResponseEntity<?> showProfile() {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuario = service.findById(autenticado.getId());

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        final Optional<UsuarioDTO> usuario = service.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombreCompleto}")
    public ResponseEntity<?> findByNombreCompleto(
            String nombreCompleto,
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
            @PathVariable String email
    ) {
        Optional<UsuarioDTO> usuario = service.findByEmail(email);
        Map<String, String> response = new HashMap<>();

        if (usuario.isEmpty()) {
            response.put("message", "No se encontraron usuarios con ese mail");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.ok(usuario);
        }
    }

    @GetMapping("/rol/{rolUsuario}")
    public ResponseEntity<?> findByRolUsuario(
            @PathVariable RolUsuario rolUsuario
    ) {
        List<UsuarioDTO> usuario = service.findByRolUsuario(rolUsuario);
        Map<String, String> response = new HashMap<>();

        if (!usuario.isEmpty()) {
            return ResponseEntity.ok(usuario);
        } else {
            response.put("message", "No se encontraron usuarios con el rol especificado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @Valid @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRolDTO userDto) {
        Optional<UsuarioDTO> usuarioActualizar = service.updateUsuarioAdmin(id, userDto);
        Map<String, String> response = new HashMap<>();

        if (usuarioActualizar.isPresent()) {
            response.put("message", "Usuario actualizado correctamente");
            return ResponseEntity.ok(usuarioActualizar.get());
        } else {

            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        boolean eliminado = service.deleteUsuarioAdmin(id);
        if (!eliminado) {
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/id/{id}/changePassword")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @RequestBody AdminPasswordUpdateDTO adminPasswordUpdateDTO
    ) {
        boolean ok = service.cambiarPasswordAdmin(id, adminPasswordUpdateDTO.getNuevaPassword());
        Map<String, String> response = new HashMap<>();
        if (ok) {
            response.put("message", "Contraseña actualizada correctamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Contraseña invalida (no puede ser la misma que la anterior) o usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
