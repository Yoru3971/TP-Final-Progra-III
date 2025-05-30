package com.viandasApp.api.User.service;

import com.viandasApp.api.User.dto.UserCreateDTO;
import com.viandasApp.api.User.dto.UserDTO;
import com.viandasApp.api.User.dto.UserUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO create(UserCreateDTO userDto);

    List<UserDTO> read();

    Optional<UserDTO> findById(Long id);

    Optional<UserDTO> update(Long id, UserUpdateDTO userDto);

    boolean delete(Long id);
}
