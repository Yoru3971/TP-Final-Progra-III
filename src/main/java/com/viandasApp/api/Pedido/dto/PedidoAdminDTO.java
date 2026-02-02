package com.viandasApp.api.Pedido.dto;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoAdminDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Usuario.dto.UsuarioAdminDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Relation(collectionRelation = "pedidoDTOList")
public class PedidoAdminDTO extends RepresentationModel<PedidoAdminDTO> {

    private Long id;
    private UsuarioAdminDTO cliente;
    private EmprendimientoAdminDTO emprendimiento;
    private LocalDate fechaEntrega;
    private Double total;
    private EstadoPedido estado;
    private List<DetalleViandaDTO> viandas;

    public PedidoAdminDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.fechaEntrega = pedido.getFechaEntrega();
        this.total = pedido.getTotal();
        this.estado = pedido.getEstado();
        this.cliente = new UsuarioAdminDTO(pedido.getUsuario());
        this.emprendimiento = new EmprendimientoAdminDTO(pedido.getEmprendimiento());
        this.viandas = pedido.getViandas()
                .stream()
                .map(DetalleViandaDTO::new)
                .toList();
    }
}
