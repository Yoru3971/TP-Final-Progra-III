package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioDTO createUsuario(UsuarioCreateDTO userDto);

    List<UsuarioDTO> readUsuarios();

    Optional<UsuarioDTO> findById(Long id);

    Optional<Usuario> findEntityById(Long id);

    Optional<UsuarioDTO> findByNombreCompleto(String nombreCompleto);

    List<UsuarioDTO> findByEmail(String email);

    List<UsuarioDTO> findByRolUsuario(RolUsuario rolUsuario);

    Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO userDto);

    boolean deleteUsuario(Long id);
}
