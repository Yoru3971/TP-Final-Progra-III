package com.viandasApp.api.Usuario.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioAdminDTO extends RepresentationModel<UsuarioAdminDTO> {
    private Long id;

    private String nombreCompleto;

    private String imagenUrl;

    private String email;

    private String telefono;

    private RolUsuario rolUsuario;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime bannedAt;

    private LocalDateTime deletedAt;

    public UsuarioAdminDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.imagenUrl = usuario.getImagenUrl();
        this.rolUsuario = usuario.getRolUsuario();
        this.enabled = usuario.isEnabled();
        this.createdAt = usuario.getCreatedAt();
        this.bannedAt = usuario.getBannedAt();
        this.deletedAt = usuario.getDeletedAt();

        if (usuario.getDeletedAt() != null) {
            this.nombreCompleto = limpiarDato(usuario.getNombreCompleto(), "usuario_borrado_");
            this.email = limpiarDato(usuario.getEmail(), "usuario_borrado_");
            this.telefono = limpiarDato(usuario.getTelefono(), "borrado_");
        } else {
            this.nombreCompleto = usuario.getNombreCompleto();
            this.email = usuario.getEmail();
            this.telefono = usuario.getTelefono();
        }
    }

    private String limpiarDato(String datoSucio, String prefijoBase) {
        if (datoSucio == null) return "";
        String regex = "^" + prefijoBase + "\\d+_";

        return datoSucio.replaceFirst(regex, "");
    }
}
