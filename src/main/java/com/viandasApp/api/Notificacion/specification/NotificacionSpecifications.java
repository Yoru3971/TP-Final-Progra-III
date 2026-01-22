package com.viandasApp.api.Notificacion.specification;

import com.viandasApp.api.Notificacion.model.Notificacion;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class NotificacionSpecifications {

    public static Specification<Notificacion> byDestinatarioId(Long destinatarioId) {
        return (root, query, cb) -> cb.equal(root.get("destinatario").get("id"), destinatarioId);
    }

    public static Specification<Notificacion> byLeida(Boolean leida) {
        return (root, query, cb) -> {
            if (leida == null) return null;
            return cb.equal(root.get("leida"), leida);
        };
    }

    public static Specification<Notificacion> byFechaDesde(LocalDate desde) {
        return (root, query, cb) -> {
            if (desde == null) return null;
            return cb.greaterThanOrEqualTo(root.get("fechaEnviado"), desde);
        };
    }

    public static Specification<Notificacion> byFechaHasta(LocalDate hasta) {
        return (root, query, cb) -> {
            if (hasta == null) return null;
            return cb.lessThanOrEqualTo(root.get("fechaEnviado"), hasta);
        };
    }
}
