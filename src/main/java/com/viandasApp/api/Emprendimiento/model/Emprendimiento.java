package com.viandasApp.api.Emprendimiento.model;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.model.Vianda;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emprendimientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Emprendimiento {
    @Id
    @Column(name = "emprendimiento_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombreEmprendimiento;

    @Column(name = "imagen_url", nullable = false)
    private String imagenUrl;

    @Column(nullable = false)
    @NotBlank
    private String ciudad;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    @NotBlank
    private String telefono;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "emprendimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vianda> viandas = new ArrayList<>();


    public Emprendimiento(String nombreEmprendimiento, String ciudad, String direccion, String telefono, Usuario usuario, String imagenUrl) {
        this.nombreEmprendimiento = nombreEmprendimiento;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.usuario = usuario;
        this.imagenUrl = imagenUrl;
    }

    @PrePersist
    public void prePersist() {
        if (this.direccion == null) {
            this.direccion = "Sin dirección";
        }
    }
}
