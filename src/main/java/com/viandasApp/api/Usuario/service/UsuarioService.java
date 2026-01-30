package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Usuario.dto.*;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    //--------------------------Create--------------------------//
    UsuarioAdminDTO createUsuario(UsuarioCreateDTO userDto);

    //--------------------------Read--------------------------//
    Page<UsuarioAdminDTO> buscarUsuarios(String nombre, String email, Pageable pageable);
    Optional<UsuarioAdminDTO> findByIdAdmin(Long id);
    Optional<UsuarioDTO> findById(Long id);
    Optional<Usuario> findEntityById(Long id);
    Optional<UsuarioAdminDTO> findByNombreCompleto(String nombreCompleto);
    Optional<UsuarioAdminDTO> findByEmail(String email);
    List<UsuarioAdminDTO> findByRolUsuario(RolUsuario rolUsuario);

    //--------------------------Update--------------------------//
    Optional<UsuarioAdminDTO> updateUsuarioAdmin(Long id, UsuarioUpdateRolDTO userDto);
    Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO dto, Usuario autenticado);
    UsuarioAdminDTO updateImagenUsuarioAdmin(Long id, MultipartFile image);
    UsuarioDTO updateImagenUsuario(Long id, MultipartFile image, Usuario autenticado);
    UsuarioAdminDTO enableUsuario(Long id);
    UsuarioAdminDTO banUsuario(Long id);
    UsuarioAdminDTO unbanUsuario(Long id);

    //--------------------------Delete--------------------------//
    boolean deleteUsuarioAdmin(Long id);
    boolean deleteUsuario(Long id, Usuario autenticado);

    //--------------------------Otros--------------------------//
    boolean cambiarPasswordAdmin(Long id, String passwordNueva);
    boolean cambiarPassword(Long id, String passwordActual, String passwordNueva, Usuario autenticado);
}
