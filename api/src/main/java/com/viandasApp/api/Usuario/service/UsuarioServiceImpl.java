package com.viandasApp.api.Usuario.service;

import ch.qos.logback.core.encoder.Encoder;
import com.viandasApp.api.Seguridad.SecurityConfig;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new SecurityConfig().passwordEncoder(); // Obtiene el PasswordEncoder de la configuración de seguridad
    }


    @Override
    public UsuarioDTO createUsuario(UsuarioCreateDTO usuarioDTO) {
        Usuario usuario = DTOToEntity(usuarioDTO);
        Usuario savedUsuario = repository.save(usuario);
        return new UsuarioDTO(savedUsuario);
    }

    @Override
    public List<UsuarioDTO> readUsuarios() {
        return repository.findAll().stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> findById(Long id) {
        return repository.findById(id).map(UsuarioDTO::new);
    }

    @Override
    public Optional<Usuario> findEntityById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<UsuarioDTO> findByNombreCompleto(String nombreCompleto) {
        return repository.findByNombreCompletoContaining(nombreCompleto).map(UsuarioDTO::new);
    }

    @Override
    public Optional<UsuarioDTO> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(UsuarioDTO::new);
    }

    @Override
    public List<UsuarioDTO> findByRolUsuario(RolUsuario rolUsuario) {
        return repository.findByRolUsuario(rolUsuario)
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO) {
        return repository.findById(id).map(
                usuarioExistente -> {
                    if (usuarioUpdateDTO.getId() != null) {
                        usuarioExistente.setId(usuarioUpdateDTO.getId());
                    }

                    if (usuarioUpdateDTO.getNombreCompleto() != null) {
                        usuarioExistente.setNombreCompleto(usuarioUpdateDTO.getNombreCompleto());
                    }

                    if (usuarioUpdateDTO.getEmail() != null) {
                        usuarioExistente.setEmail(usuarioUpdateDTO.getEmail());
                    }

                    if (usuarioUpdateDTO.getRolUsuario() != null) {
                        usuarioExistente.setRolUsuario(usuarioUpdateDTO.getRolUsuario());
                    }

                    final Usuario updatedUsuario = repository.save(usuarioExistente);
                    return new UsuarioDTO(updatedUsuario);
                }
        );
    }

    @Override
    public boolean deleteUsuario(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }

    private Usuario DTOToEntity(UsuarioCreateDTO usuarioCreateDTO) {
        return new Usuario(
                usuarioCreateDTO.getId(),
                usuarioCreateDTO.getNombreCompleto(),
                usuarioCreateDTO.getEmail(),
                passwordEncoder.encode(usuarioCreateDTO.getPassword()),
                usuarioCreateDTO.getRolUsuario()
        );
    }
}
