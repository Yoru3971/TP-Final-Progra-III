package com.viandasApp.api.Pedido.model;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    private LocalDate fecha;

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
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