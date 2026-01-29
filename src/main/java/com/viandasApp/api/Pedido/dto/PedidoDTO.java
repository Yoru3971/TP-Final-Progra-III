package com.viandasApp.api.Pedido.dto;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class PedidoDTO extends RepresentationModel<PedidoDTO>{

    private Long id;
    private UsuarioDTO cliente;
    private LocalDate fechaEntrega;
    private Double total;
    private EstadoPedido estado;
    private EmprendimientoDTO emprendimiento;
    private List<DetalleViandaDTO> viandas;

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.cliente = new UsuarioDTO(pedido.getUsuario());
        this.fechaEntrega = pedido.getFechaEntrega();
        this.total = pedido.getTotal();
        this.estado = pedido.getEstado();
        this.emprendimiento = new EmprendimientoDTO(pedido.getEmprendimiento());
        this.viandas = pedido.getViandas()
                .stream()
                .map(DetalleViandaDTO::new)
                .toList();


    }
}
