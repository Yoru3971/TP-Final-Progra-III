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
    private LocalDate fecha;
    private Double total;
    private List<DetalleViandaDTO> viandas;

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.clienteId = pedido.getCliente().getId();
        this.estado = pedido.getEstado();
        this.fecha = pedido.getFecha();

        this.viandas = pedido.getViandas().stream()
                .map(DetalleViandaDTO::new)
                .toList();

        this.total = viandas.stream()
                .mapToDouble(DetalleViandaDTO::getSubtotal)
                .sum();
    }

}
