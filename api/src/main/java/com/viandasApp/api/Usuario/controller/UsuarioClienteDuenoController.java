package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.dto.PasswordUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioClienteDuenoController {
    private final UsuarioService usuarioService;

    //--------------------------Update--------------------------//
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        Map<String, Object> response = new HashMap<>();

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuarioActualizar = usuarioService.updateUsuario(id, usuarioUpdateDTO, autenticado);
        response.put("Usuario actualizado correctamente",usuarioActualizar );
        return ResponseEntity.ok(usuarioActualizar.get());
    }

    //--------------------------Delete--------------------------//
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long id) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();
        usuarioService.deleteUsuario(id, autenticado);

        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    //--------------------------Otros--------------------------//
    @PutMapping("/changePassword/me")
    public ResponseEntity<?> cambiarPassword(
            @RequestBody PasswordUpdateDTO passwordUpdateDTO
    ) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        usuarioService.cambiarPassword(autenticado.getId(), passwordUpdateDTO.getPasswordActual(), passwordUpdateDTO.getPasswordNueva(), autenticado);
        Map<String, String> response = new HashMap<>();

        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> showProfile() {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuario = usuarioService.findById(autenticado.getId());

        return ResponseEntity.ok(usuario.get());
    }
}
