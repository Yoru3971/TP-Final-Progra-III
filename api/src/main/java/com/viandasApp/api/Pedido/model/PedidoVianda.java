package com.viandasApp.api.Pedido.model;

import com.viandasApp.api.Vianda.model.Vianda;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoVianda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vianda_id")
    private Vianda vianda;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
