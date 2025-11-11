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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Usuarios - Cliente/Dueño")
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioClienteDuenoController {
    private final UsuarioService usuarioService;

    //--------------------------Update--------------------------//
     @Operation(
            summary = "Actualizar perfil del usuario autenticado",
            description = "Permite al usuario autenticado actualizar sus datos",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        Map<String, Object> response = new HashMap<>();

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<UsuarioDTO> usuarioActualizar = usuarioService.updateUsuario(id, usuarioUpdateDTO, autenticado);
        response.put("Usuario actualizado correctamente",usuarioActualizar );
        return ResponseEntity.ok(usuarioActualizar.get());
    }
    
    @Operation(
            summary = "Cambiar contraseña del usuario autenticado",
            description = "Permite al usuario autenticado cambiar su contraseña",
            security = @SecurityRequirement(name = "bearer-jwt")
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

        usuarioService.cambiarPassword(autenticado.getId(), passwordUpdateDTO.getPasswordActual(), passwordUpdateDTO.getPasswordNueva(), autenticado);
        Map<String, String> response = new HashMap<>();

        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }
  
    //--------------------------Delete--------------------------//
    @Operation(
            summary = "Eliminar usuario",
            description = "Permite al usuario autenticado eliminar su cuenta",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        usuarioService.deleteUsuario(id, autenticado);

        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    //--------------------------Otros--------------------------//
    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Devuelve los datos del usuario autenticado",
            security = @SecurityRequirement(name = "bearer-jwt")
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

        return ResponseEntity.ok(usuario.get());
    }
}
