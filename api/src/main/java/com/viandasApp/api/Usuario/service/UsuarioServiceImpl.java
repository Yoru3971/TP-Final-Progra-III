package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
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
    public UsuarioDTO create(UsuarioCreateDTO userDto) {
        final Usuario usuario = DTOToEntity(userDto);
        final Usuario savedUsuario = repository.save(usuario);
        return EntityToDTO(savedUsuario);
    }

    @Override
    public List<UsuarioDTO> read() {
        return repository.findAll().stream()
                .map(this::EntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> findById(Long id) {
        return repository.findById(id).map(this::EntityToDTO);
    }

    @Override
    public Optional<UsuarioDTO> findByEmail(String email) {
        return repository.findAll().stream()
                .filter((user) -> user.getEmail().equals(email))
                .map(this::EntityToDTO)
                .findFirst();
    }

    @Override
    public Optional<UsuarioDTO> update(Long id, UsuarioUpdateDTO userDto) {
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
                    return EntityToDTO(updatedUsuario);
                }
        );
    }

    @Override
    public boolean delete(Long id) {
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

    private UsuarioDTO EntityToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getRolUsuario()
        );
    }

    @Override
    public Optional<Usuario> findEntityById(Long id) {
        return repository.findById(id);
    }

}
