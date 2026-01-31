package com.viandasApp.api.Reclamo.specification;

import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import org.springframework.data.jpa.domain.Specification;

public class ReclamoSpecifications {

    public static Specification<Reclamo> porEmailUsuarioExacto(String email) {
        return (root, query, cb) -> cb.equal(root.get("emailUsuario"), email);
    }

    public static Specification<Reclamo> porEmailUsuarioContiene(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return null;
            return cb.like(cb.lower(root.get("emailUsuario")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<Reclamo> porEstado(EstadoReclamo estado) {
        return (root, query, cb) -> {
            if (estado == null) return null;
            return cb.equal(root.get("estado"), estado);
        };
    }
}
