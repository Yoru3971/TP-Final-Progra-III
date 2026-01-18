package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    //--------------------------Create--------------------------//
    UsuarioDTO createUsuario(UsuarioCreateDTO userDto);

    //--------------------------Read--------------------------//
    List<UsuarioDTO> readUsuarios();
    Optional<UsuarioDTO> findById(Long id);
    Optional<Usuario> findEntityById(Long id);
    Optional<UsuarioDTO> findByNombreCompleto(String nombreCompleto);
    Optional<UsuarioDTO> findByEmail(String email);
    List<UsuarioDTO> findByRolUsuario(RolUsuario rolUsuario);

    //--------------------------Update--------------------------//
    Optional<UsuarioDTO> updateUsuarioAdmin(Long id, UsuarioUpdateRolDTO userDto);
    Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO dto, Usuario autenticado);
    UsuarioDTO updateImagenUsuarioAdmin(Long id, MultipartFile image);
    UsuarioDTO updateImagenUsuario(Long id, MultipartFile image, Usuario autenticado);

    //--------------------------Delete--------------------------//
    boolean deleteUsuarioAdmin(Long id);
    boolean deleteUsuario(Long id, Usuario autenticado);

    //--------------------------Otros--------------------------//
    boolean cambiarPasswordAdmin(Long id, String passwordNueva);
    boolean cambiarPassword(Long id, String passwordActual, String passwordNueva, Usuario autenticado);
}
