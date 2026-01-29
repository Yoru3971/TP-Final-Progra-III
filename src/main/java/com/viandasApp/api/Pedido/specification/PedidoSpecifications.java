package com.viandasApp.api.Pedido.specification;

import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PedidoSpecifications {

    public static Specification<Pedido> hasEstado(EstadoPedido estado) {
        return (root, query, cb) -> {
            if (estado == null) return null;
            return cb.equal(root.get("estado"), estado);
        };
    }

    public static Specification<Pedido> fechaEntregaEntre(LocalDate desde, LocalDate hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) return null;
            if (desde != null && hasta != null) {
                return cb.between(root.get("fechaEntrega"), desde, hasta);
            } else if (desde != null) {
                return cb.greaterThanOrEqualTo(root.get("fechaEntrega"), desde);
            } else {
                return cb.lessThanOrEqualTo(root.get("fechaEntrega"), hasta);
            }
        };
    }

    public static Specification<Pedido> hasNombreEmprendimiento(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isEmpty()) return null;
            return cb.like(cb.lower(root.get("emprendimiento").get("nombreEmprendimiento")), "%" + nombre.toLowerCase() + "%");
        };
    }

    //  --------------------------Seguridad--------------------------//

    // Solo pedidos del cliente
    public static Specification<Pedido> delCliente(Long clienteId) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("id"), clienteId);
    }

    // Solo pedidos de los emprendimientos del dueño
    public static Specification<Pedido> delDueno(Long duenoId) {
        return (root, query, cb) -> cb.equal(root.get("emprendimiento").get("usuario").get("id"), duenoId);
    }

    //  --------------------------Ordenamiento--------------------------//
    //  El orden de los pedidos:
    //  - Los pedidos cancelados o con fecha pasada, van al final
    //  - Después, ordeno para mostrar los pedidos de hoy en adelante
    public static Specification<Pedido> conOrdenamientoDefecto() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {

                var caseEstado = cb.selectCase()
                        .when(cb.equal(root.get("estado"), EstadoPedido.CANCELADO), 1)
                        .otherwise(0);

                var caseFecha = cb.selectCase()
                        .when(cb.lessThan(root.get("fechaEntrega"), LocalDate.now()), 1)
                        .otherwise(0);

                query.orderBy(
                        cb.asc(caseEstado),
                        cb.asc(caseFecha),
                        cb.asc(root.get("fechaEntrega"))
                );
            }
            return null;
        };
    }
}
