package com.viandasApp.api.Pedido.mappers;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public Pedido DTOToEntity(PedidoCreateDTO dto, Usuario cliente, Emprendimiento emprendimiento) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(cliente);
        pedido.setEmprendimiento(emprendimiento);
        pedido.setFechaEntrega(dto.getFechaEntrega());
        return pedido;
    }
}
