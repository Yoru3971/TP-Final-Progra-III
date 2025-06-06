package com.viandasApp.api.User.dto;

import com.viandasApp.api.User.model.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserCreateDTO {
    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 1, max = 255, message = "El nombre debe tener [min, max] caracteres.")
    private String fullName;

    @Email
    @NotBlank(message = "El email es obligatorio.")
    @Size(min = 1, max = 64, message = "El email debe tener [min, max] caracteres.")
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El rol es obligatorio.")
    private UserRole role;
//
//    public UserCreateDTO(Long id, String fullName, String email, UserRole role) {
//        this.id = id;
//        this.fullName = fullName;
//        this.email = email;
//        this.role = role;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public UserRole getRole() {
//        return role;
//    }
//
//    public void setRole(UserRole role) {
//        this.role = role;
//    }
}
