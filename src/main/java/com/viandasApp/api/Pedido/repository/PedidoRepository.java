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
    List<Pedido> findByEmprendimientoId(Long idEmprendimiento);
    List<Pedido> findByEmprendimientoIdAndUsuarioId(Long idEmprendimiento, Long idUsuario);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByFechaEntrega(LocalDate fechaEntrega);
    List<Pedido> findByFechaEntregaAndUsuarioId(LocalDate fechaEntrega, Long idUsuario);
    List<Pedido> findByFechaEntregaAndEmprendimientoId(LocalDate fechaEntrega, Long idEmprendimiento);
    List<Pedido> findByEmprendimientoUsuarioId(Long idDueno); //Busca pedidos donde pedido.emprendimiento.usuario.id coincide con el enviado por parametro”
}
