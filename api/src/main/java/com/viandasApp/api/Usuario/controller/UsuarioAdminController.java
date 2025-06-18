package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.dto.AdminPasswordUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {
    private final UsuarioService usuarioService;

    //--------------------------Create--------------------------//
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(usuarioCreateDTO);
        response.put("message", "Usuario registrado correctamente");
        return ResponseEntity.ok(nuevoUsuario);
    }

    //--------------------------Read--------------------------//
    @GetMapping
    public ResponseEntity<?> readUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.readUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario.get());
    }

    @GetMapping("/nombre/{nombreCompleto}")
    public ResponseEntity<?> findByNombreCompleto(
            String nombreCompleto
    ) {
        Optional<UsuarioDTO> usuarioEncontrado = usuarioService.findByNombreCompleto(nombreCompleto);
        return ResponseEntity.ok(usuarioEncontrado.get());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(
            @PathVariable String email
    ) {
        Optional<UsuarioDTO> usuario = usuarioService.findByEmail(email);

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/rol/{rolUsuario}")
    public ResponseEntity<?> findByRolUsuario(
            @PathVariable RolUsuario rolUsuario
    ) {
        List<UsuarioDTO> usuarios = usuarioService.findByRolUsuario(rolUsuario);

        return ResponseEntity.ok(usuarios);
    }

    //--------------------------Update--------------------------//
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @Valid @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRolDTO userDto) {
        Optional<UsuarioDTO> usuarioActualizar = usuarioService.updateUsuarioAdmin(id, userDto);
        Map<String, Object> response = new HashMap<>();

        response.put("Usuario actualizado correctamente", usuarioActualizar);
        return ResponseEntity.ok(usuarioActualizar.get());
    }

    @PutMapping("/id/{id}/changePassword")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id, @RequestBody AdminPasswordUpdateDTO adminPasswordUpdateDTO) {
        usuarioService.cambiarPasswordAdmin(id, adminPasswordUpdateDTO.getNuevaPassword());
        Map<String, String> response = new HashMap<>();

        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        usuarioService.deleteUsuarioAdmin(id);
        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    //--------------------------Otros--------------------------//
    @GetMapping("/me")
    public ResponseEntity<?> showProfile() {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuario = usuarioService.findById(autenticado.getId());

        return ResponseEntity.ok(usuario.get());
    }
}
