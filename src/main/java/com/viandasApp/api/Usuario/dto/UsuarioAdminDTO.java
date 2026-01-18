package com.viandasApp.api.Usuario.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioAdminDTO {
    private Long id;

    private String nombreCompleto;

    private String imagenUrl;

    private String email;

    private String telefono;

    private RolUsuario rolUsuario;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    public UsuarioAdminDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.imagenUrl = usuario.getImagenUrl();
        this.email = usuario.getEmail();
        this.telefono = usuario.getTelefono();
        this.rolUsuario = usuario.getRolUsuario();
        this.enabled = usuario.isEnabled();
        this.createdAt = usuario.getCreatedAt();
        this.deletedAt = usuario.getDeletedAt();
    }
}
