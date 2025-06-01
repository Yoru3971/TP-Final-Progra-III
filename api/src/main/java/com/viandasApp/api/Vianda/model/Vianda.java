package com.viandasApp.api.Vianda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name="viandas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vianda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombreVianda;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private CategoriaVianda categoria;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Boolean esVegano;

    @Column(nullable = false)
    private Boolean esVegetariano;

    @Column(nullable = false)
    private Boolean esSinTacc;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "emprendimiento_id", nullable = false)
    // private Emprendimiento emprendimiento;

}
