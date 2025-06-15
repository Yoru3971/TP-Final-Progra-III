package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UsuarioDTO createUsuario(UsuarioCreateDTO userDto) {
        final Usuario usuario = DTOToEntity(userDto);
        final Usuario savedUsuario = repository.save(usuario);
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
    public List<UsuarioDTO> findByEmail(String email) {
        return repository.findByEmail(email)
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTO> findByRolUsuario(RolUsuario rolUsuario) {
        return repository.findByRolUsuario(rolUsuario)
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO userDto) {
        return repository.findById(id).map(
                existingUser -> {
                    if (userDto.getId() != null) {
                        existingUser.setId(userDto.getId());
                    }

                    if (userDto.getNombreCompleto() != null) {
                        existingUser.setNombreCompleto(userDto.getNombreCompleto());
                    }

                    if (userDto.getEmail() != null) {
                        existingUser.setEmail(userDto.getEmail());
                    }

                    if (userDto.getRolUsuario() != null) {
                        existingUser.setRolUsuario(userDto.getRolUsuario());
                    }

                    final Usuario updatedUsuario = repository.save(existingUser);
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

    private Usuario DTOToEntity(UsuarioCreateDTO userDto) {
        return new Usuario(
                userDto.getId(),
                userDto.getNombreCompleto(),
                userDto.getEmail(),
                userDto.getRolUsuario()
        );
    }
}
