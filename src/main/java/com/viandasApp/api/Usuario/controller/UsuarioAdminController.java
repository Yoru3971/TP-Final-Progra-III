package com.viandasApp.api.Usuario.controller;

import com.viandasApp.api.Usuario.dto.AdminPasswordUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Usuarios - Admin")
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {
    private final UsuarioService usuarioService;

    //--------------------------Create--------------------------//
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Permite al administrador registrar un nuevo usuario en el sistema",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        Map<String, Object> response = new HashMap<>();
        UsuarioDTO nuevoUsuario = usuarioService.createUsuario(usuarioCreateDTO);
        response.put("message", "Usuario registrado correctamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve una lista de todos los usuarios registrados en el sistema",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        return ResponseEntity.ok(usuarios);
    }
    
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve un usuario específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario.get());
    }

    @Operation(
            summary = "Obtener usuario por nombre completo",
            description = "Devuelve un usuario específico por su nombre completo",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        return ResponseEntity.ok(usuarioEncontrado.get());
    }

    @Operation(
            summary = "Obtener usuario por email",
            description = "Devuelve un usuario específico por su email",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Obtener usuarios por rol",
            description = "Devuelve una lista de usuarios que tienen un rol específico",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        List<UsuarioDTO> usuarios = usuarioService.findByRolUsuario(rolUsuario);

        return ResponseEntity.ok(usuarios);
    }
    
    //--------------------------Update--------------------------//
    @Operation(
            summary = "Actualizar usuario",
            description = "Permite al administrador actualizar los datos de un usuario existente",
            security = @SecurityRequirement(name = "bearer-jwt")
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
        Map<String, Object> response = new HashMap<>();

        response.put("Usuario actualizado correctamente", usuarioActualizar);
        return ResponseEntity.ok(usuarioActualizar.get());
    }

    @Operation(
            summary = "Actualizar foto de perfil",
            description = "Actualiza la imagen de perfil de un usuario.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o formato no permitido"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "No tenés permiso para editar este perfil"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/{id}/imagen", consumes = "multipart/form-data")
    public ResponseEntity<UsuarioDTO> updateImagenUsuario(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UsuarioDTO usuarioActualizado = usuarioService.updateImagenUsuarioAdmin(id, image);

        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(
            summary = "Cambiar contraseña de un usuario",
            description = "Permite al administrador cambiar la contraseña de un usuario",
            security = @SecurityRequirement(name = "bearer-jwt")
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
            @PathVariable Long id, @RequestBody AdminPasswordUpdateDTO adminPasswordUpdateDTO) {
        usuarioService.cambiarPasswordAdmin(id, adminPasswordUpdateDTO.getNuevaPassword());
        Map<String, String> response = new HashMap<>();

        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//   
     @Operation(
            summary = "Eliminar usuario",
            description = "Permite al administrador eliminar un usuario del sistema",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        usuarioService.deleteUsuarioAdmin(id);
        response.put("message", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }
      
   //--------------------------Otros--------------------------//
    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Devuelve el perfil del usuario actualmente autenticado",
            security = @SecurityRequirement(name = "bearer-jwt")
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

        return ResponseEntity.ok(usuario.get());
    }
}
