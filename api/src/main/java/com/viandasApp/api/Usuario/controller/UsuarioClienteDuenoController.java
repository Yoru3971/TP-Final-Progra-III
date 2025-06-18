package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.dto.PasswordUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Usuarios - Cliente/Dueño", description = "Controlador para gestionar usuarios del cliente y dueño")
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAuthority('ROLE_DUENO') or hasAuthority('ROLE_CLIENTE')")
public class UsuarioClienteDuenoController {
    private final UsuarioService usuarioService;

    public UsuarioClienteDuenoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Devuelve los datos del usuario autenticado",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/me")
    public ResponseEntity<?> showProfile() {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuario = usuarioService.findById(autenticado.getId());

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Actualizar perfil del usuario autenticado",
            description = "Permite al usuario autenticado actualizar sus datos",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        Map<String, String> response = new HashMap<>();

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuarioActualizar = usuarioService.findById(id);

        if(usuarioActualizar.isPresent()){
            response.put("message", "Usuario no encontrado o acceso denegado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<UsuarioDTO> usuarioActualizado = usuarioService.updateUsuario(id, usuarioUpdateDTO, autenticado);
        response.put("message", "Usuario actualizado correctamente");
        return ResponseEntity.ok(usuarioActualizado.get());
    }

    @Operation(
            summary = "Cambiar contraseña del usuario autenticado",
            description = "Permite al usuario autenticado cambiar su contraseña",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos"),
            @ApiResponse(responseCode = "403", description = "Contraseña actual incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/changePassword/me")
    public ResponseEntity<?> cambiarPassword(
            @RequestBody PasswordUpdateDTO passwordUpdateDTO
    ) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean ok = usuarioService.cambiarPassword(autenticado.getId(), passwordUpdateDTO.getPasswordActual(), passwordUpdateDTO.getPasswordNueva(), autenticado);
        Map<String, String> response = new HashMap<>();

        if (!ok) {
            response.put("message", "Contraseña actual incorrecta");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Permite al usuario autenticado eliminar su cuenta",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado o usuario no encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long id) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();
        boolean fueEliminado = usuarioService.deleteUsuario(id, autenticado);

        if (!fueEliminado) {
            response.put("message", "Acceso denegado o usuario no encontrado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}
