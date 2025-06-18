package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.dto.AdminPasswordUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import com.viandasApp.api.Utils.ErrorHandler;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Usuarios - Admin", description = "Controlador para gestionar usuarios con rol de administrador")
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UsuarioAdminController {
    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Permite al administrador registrar un nuevo usuario en el sistema",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorHandler.procesarErrores(result));
        }
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(usuarioCreateDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve una lista de todos los usuarios registrados en el sistema",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<?> readUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.readUsuarios();

        if (usuarios.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No hay usuarios registrados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Devuelve el perfil del usuario actualmente autenticado",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
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
            summary = "Obtener usuario por ID",
            description = "Devuelve un usuario específico por su ID",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        final Optional<UsuarioDTO> usuario = usuarioService.findById(id);

        if (usuario.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Si el usuario existe, devolvemos el DTO
        return ResponseEntity.ok(usuario.get());
    }

    @Operation(
            summary = "Obtener usuario por nombre completo",
            description = "Devuelve un usuario específico por su nombre completo",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/nombre/{nombreCompleto}")
    public ResponseEntity<?> findByNombreCompleto(
            String nombreCompleto
    ) {
        Optional<UsuarioDTO> usuarioEncontrado = usuarioService.findByNombreCompleto(nombreCompleto);

        if (usuarioEncontrado.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontró ningún usuario con ese nombre completo");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(usuarioEncontrado.get());
    }

    @Operation(
            summary = "Obtener usuario por email",
            description = "Devuelve un usuario específico por su email",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(
            @PathVariable String email
    ) {
        Optional<UsuarioDTO> usuario = usuarioService.findByEmail(email);
        Map<String, String> response = new HashMap<>();

        if (usuario.isEmpty()) {
            response.put("message", "No se encontraron usuarios con ese mail");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Obtener usuarios por rol",
            description = "Devuelve una lista de usuarios que tienen un rol específico",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios con el rol especificado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/rol/{rolUsuario}")
    public ResponseEntity<?> findByRolUsuario(
            @PathVariable RolUsuario rolUsuario
    ) {
        List<UsuarioDTO> usuario = usuarioService.findByRolUsuario(rolUsuario);
        Map<String, String> response = new HashMap<>();

        if (usuario.isEmpty()) {
            response.put("message", "No se encontraron usuarios con el rol especificado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Permite al administrador actualizar los datos de un usuario existente",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @Valid @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRolDTO userDto) {
        Optional<UsuarioDTO> usuarioActualizar = usuarioService.updateUsuarioAdmin(id, userDto);
        Map<String, String> response = new HashMap<>();

        if (usuarioActualizar.isPresent()) {
            response.put("message", "Usuario actualizado correctamente");
            return ResponseEntity.ok(usuarioActualizar.get());
        } else {
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Permite al administrador eliminar un usuario del sistema",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        Optional<UsuarioDTO> usuarioEliminar = usuarioService.findById(id);

        if (usuarioEliminar.isEmpty()) {
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        usuarioService.deleteUsuarioAdmin(id);
        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cambiar contraseña de un usuario",
            description = "Permite al administrador cambiar la contraseña de un usuario",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, contraseña incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o contraseña inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}/changePassword")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @RequestBody AdminPasswordUpdateDTO adminPasswordUpdateDTO
    ) {
        boolean ok = usuarioService.cambiarPasswordAdmin(id, adminPasswordUpdateDTO.getNuevaPassword());
        Map<String, String> response = new HashMap<>();

        if (!ok) {
            response.put("message", "Contraseña invalida (no puede ser la misma que la anterior) o usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }
}
