package com.viandasApp.api.Usuario.mappers;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public Usuario DTOToEntity(UsuarioCreateDTO dto) {
        String imagenPorDefecto = "https://res.cloudinary.com/dsgqbotzi/image/upload/v1765496442/usuario_por_defecto_dtac7c.jpg";

        return new Usuario(
                null,
                dto.getNombreCompleto(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelefono(),
                dto.getRolUsuario(),
                imagenPorDefecto
        );
    }
}
