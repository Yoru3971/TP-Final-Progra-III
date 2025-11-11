package com.viandasApp.api.Vianda.model;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Pedido.model.DetallePedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "viandas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vianda {
    @Id
    @Column(name = "vianda_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombreVianda;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private CategoriaVianda categoria;

    @Column(nullable = false)
    @NotBlank
    private String descripcion;

    @Column(name = "imagen_url", nullable = false)
    private String imagenUrl;

    @Column(nullable = false)
    @NotNull
    private Double precio;

    @Column(nullable = false)
    private Boolean esVegano;

    @Column(nullable = false)
    private Boolean esVegetariano;

    @Column(nullable = false)
    private Boolean esSinTacc;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emprendimiento_id", nullable = false)
    private Emprendimiento emprendimiento;

    @Column(nullable = false)
    private Boolean estaDisponible;

    @OneToMany(mappedBy = "vianda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    public Vianda(String nombreVianda, CategoriaVianda categoria, String descripcion, Double precio, Boolean esVegano
            , Boolean esVegetariano, Boolean esSinTacc, Emprendimiento emprendimiento, String imagenUrl) {
        this.nombreVianda = nombreVianda;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.esVegano = esVegano;
        this.esVegetariano = esVegetariano;
        this.esSinTacc = esSinTacc;
        this.emprendimiento = emprendimiento;
        this.estaDisponible = true;
        this.imagenUrl = imagenUrl;
    }
}
