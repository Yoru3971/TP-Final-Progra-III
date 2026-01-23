package com.viandasApp.api.Pedido.repository;

import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByEmprendimientoId(Long idEmprendimiento);
    boolean existsByEstadoAndUsuarioId(EstadoPedido estado, Long usuarioId);
    boolean existsByEstadoAndEmprendimientoUsuarioId(EstadoPedido estado, Long duenoId);
    boolean existsByEmprendimientoId(Long idEmprendimiento);

    //  Consultas para obtener los nombres de los emprendimientos distintos (dependiendo de los pedidos)

    @Query("SELECT DISTINCT p.emprendimiento.nombreEmprendimiento FROM Pedido p ORDER BY p.emprendimiento.nombreEmprendimiento")
    List<String> findDistinctEmprendimientosAdmin();

    @Query("SELECT DISTINCT p.emprendimiento.nombreEmprendimiento FROM Pedido p WHERE p.emprendimiento.usuario.id = :duenoId ORDER BY p.emprendimiento.nombreEmprendimiento")
    List<String> findDistinctEmprendimientosDueno(Long duenoId);

    @Query("SELECT DISTINCT p.emprendimiento.nombreEmprendimiento FROM Pedido p WHERE p.usuario.id = :clienteId ORDER BY p.emprendimiento.nombreEmprendimiento")
    List<String> findDistinctEmprendimientosCliente(Long clienteId);

}
