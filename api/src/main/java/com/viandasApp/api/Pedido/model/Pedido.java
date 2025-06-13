package com.viandasApp.api.Pedido.model;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    private Usuario cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emprendimiento_id")
    private Emprendimiento emprendimiento;

    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoVianda> items = new ArrayList<>();
}
