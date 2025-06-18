package com.viandasApp.api.Pedido.model;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emprendimiento_id")
    private Emprendimiento emprendimiento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> viandas = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoPedido estado;

    @Column(nullable = false)
    @NotNull
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Double total;

    @PrePersist
    public void prePersist() {
        if (this.fechaEntrega == null) {
            this.fechaEntrega = LocalDate.now();
        }
        if (this.estado == null) {
            this.estado = EstadoPedido.PENDIENTE;
        }
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.viandas.add(detalle);
    }
}