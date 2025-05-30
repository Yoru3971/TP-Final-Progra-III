package com.viandasApp.api.User.service;

import com.viandasApp.api.User.dto.UserCreateDTO;
import com.viandasApp.api.User.dto.UserDTO;
import com.viandasApp.api.User.dto.UserUpdateDTO;
import com.viandasApp.api.User.model.User;
import com.viandasApp.api.User.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDTO create(UserCreateDTO userDto) {
        final User user = DTOToEntity(userDto);
        final User savedUser = repository.save(user);
        return EntityToDTO(savedUser);
    }

    @Override
    public List<UserDTO> read() {
        return repository.findAll().stream()
                .map(this::EntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return repository.findById(id).map(this::EntityToDTO);
    }

    @Override
    public Optional<UserDTO> update(Long id, UserUpdateDTO userDto) {
        return repository.findById(id).map(
                existingUser -> {
                    if (userDto.getId() != null) {
                        existingUser.setId(userDto.getId());
                    }

                    if (userDto.getFullName() != null) {
                        existingUser.setFullName(userDto.getFullName());
                    }

                    if (userDto.getEmail() != null) {
                        existingUser.setEmail(userDto.getEmail());
                    }

                    if (userDto.getRole() != null) {
                        existingUser.setRole(userDto.getRole());
                    }

                    final User updatedUser = repository.save(existingUser);
                    return EntityToDTO(updatedUser);
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

    private User DTOToEntity(UserCreateDTO userDto) {
        return new User(
                userDto.getId(),
                userDto.getFullName(),
                userDto.getEmail(),
                userDto.getRole()
        );
    }

    private UserDTO EntityToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
