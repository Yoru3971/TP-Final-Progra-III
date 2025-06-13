package com.viandasApp.api.User.service;

import com.viandasApp.api.User.dto.UsuarioCreateDTO;
import com.viandasApp.api.User.dto.UsuarioDTO;
import com.viandasApp.api.User.dto.UsuarioUpdateDTO;
import com.viandasApp.api.User.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioDTO create(UsuarioCreateDTO userDto);

    List<UsuarioDTO> read();

    Optional<UsuarioDTO> findById(Long id);

    Optional<UsuarioDTO> findByEmail(String email);

    Optional<UsuarioDTO> update(Long id, UsuarioUpdateDTO userDto);

    boolean delete(Long id);

    Optional<Usuario> findEntityById(Long id);
}
