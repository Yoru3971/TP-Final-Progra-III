package com.viandasApp.api.Pedido.dto;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class PedidoDTO {

    private Long id;
    private Long clienteId;
    private EstadoPedido estado;
    private LocalDate fechaEntrega;
    private Double total;
    private List<DetalleViandaDTO> viandas;

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.clienteId = pedido.getUsuario().getId();
        this.estado = pedido.getEstado();
        this.fechaEntrega = pedido.getFechaEntrega();

        this.viandas = pedido.getViandas().stream()
                .map(DetalleViandaDTO::new)
                .toList();

        this.total = pedido.getTotal();
    }

}
