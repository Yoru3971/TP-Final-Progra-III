package com.viandasApp.api.Usuario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @Column(name = "usuario_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RolUsuario rolUsuario;
}
