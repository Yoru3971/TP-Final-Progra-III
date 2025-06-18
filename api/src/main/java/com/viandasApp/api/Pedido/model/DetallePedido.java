package com.viandasApp.api.Pedido.model;

import com.viandasApp.api.Vianda.model.Vianda;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "viandas_por_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vianda_id")
    private Vianda vianda;

    private Integer cantidad;

    // Se puede obtener a partir de la vianda, pero se guarda para evitar inconsistencias
    // (ya que si el precio de la vianda cambia después, me modificaría el subtotal y el total del pedido)
    private Double precioUnitario;

    private Double subtotal;

    public DetallePedido(Pedido pedido, Vianda vianda, @NotNull @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
    }
}
