package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Pedido.dto.PedidoDTO;
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
@RequestMapping("/api/public/usuarios")
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
            @Valid @PathVariable RolUsuario rolUsuario
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
            @Valid @RequestBody UsuarioUpdateDTO userDto) {
        Optional<UsuarioDTO> usuarioActualizar = service.updateUsuario(id, userDto);
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
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long id) {
        Optional<UsuarioDTO> usuarioEliminar = service.findById(id);
        Map<String, String> response = new HashMap<>();

        if(usuarioEliminar.isPresent()){
            service.deleteUsuario(id);
            response.put("message", "Usuario eliminado correctamente");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("message", "Usuario no encontrado");
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
