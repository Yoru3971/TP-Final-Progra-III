package com.viandasApp.api.Pedido.repository;

import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long idCliente);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByFecha(LocalDate fecha);
}
