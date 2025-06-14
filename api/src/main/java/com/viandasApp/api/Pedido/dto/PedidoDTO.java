package com.viandasApp.api.Pedido.dto;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Usuario.model.Usuario;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private Usuario cliente;
    private Emprendimiento emprendimiento;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private List<ItemPedidoDTO> items;
}
