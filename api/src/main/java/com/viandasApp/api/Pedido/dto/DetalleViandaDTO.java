package com.viandasApp.api.Pedido.dto;

import com.viandasApp.api.Pedido.model.DetallePedido;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetalleViandaDTO {

    private Long viandaId;
    private String nombreVianda;
    private Double precioUnitario;
    private Integer cantidad;
    private Double subtotal;

    public DetalleViandaDTO(DetallePedido detalle) {
        this.viandaId = detalle.getVianda().getId();
        this.nombreVianda = detalle.getVianda().getNombreVianda();
        this.precioUnitario = detalle.getPrecioUnitario();
        this.cantidad = detalle.getCantidad();
        this.subtotal = detalle.getSubtotal();
    }
}