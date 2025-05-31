package com.viandasApp.api.User.controller;

import com.viandasApp.api.User.dto.UserCreateDTO;
import com.viandasApp.api.User.dto.UserDTO;
import com.viandasApp.api.User.dto.UserUpdateDTO;
import com.viandasApp.api.User.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(
            @Valid @RequestBody UserCreateDTO userDto
    ) {
        final UserDTO createdUser = service.create(userDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> read() {
        final List<UserDTO> users = service.read();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        final Optional<UserDTO> user = service.findById(id);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> findByEmail(@PathVariable String email) {
        final Optional<UserDTO> user = service.findByEmail(email);

        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userDto
    ) {
        final Optional<UserDTO> updatedUser = service.update(id, userDto);

        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
