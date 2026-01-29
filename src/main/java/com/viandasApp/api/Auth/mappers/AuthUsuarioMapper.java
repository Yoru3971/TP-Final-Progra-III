package com.viandasApp.api.Auth.mappers;

import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.Usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public Usuario DTOToEntity(UsuarioRegisterDTO dto) {

        String imagenPorDefecto = "https://res.cloudinary.com/dsgqbotzi/image/upload/v1765496442/usuario_por_defecto_dtac7c.jpg";

        Usuario u = new Usuario(
                null,
                dto.getNombreCompleto(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelefono(),
                dto.getRolUsuario(),
                imagenPorDefecto
        );
        u.setEnabled(false);
        return u;
    }
}
