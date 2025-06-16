package com.viandasApp.api.Usuario.dto;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;

    private String nombreCompleto;

    private String email;

    private RolUsuario rolUsuario;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.email = usuario.getEmail();
        this.rolUsuario = usuario.getRolUsuario();
    }
}
