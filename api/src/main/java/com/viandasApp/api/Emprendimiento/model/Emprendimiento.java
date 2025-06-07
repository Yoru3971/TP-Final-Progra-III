package com.viandasApp.api.Emprendimiento.model;

import com.viandasApp.api.User.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emprendimientos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Emprendimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombreEmprendimiento;

    @Column(nullable = false)
    @NotBlank
    private String ciudad;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    @NotBlank
    private String telefono;

    @ManyToOne(optional = false)    //  muchos emprendimientos pueden pertenecer a un mismo dueño
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;


}
