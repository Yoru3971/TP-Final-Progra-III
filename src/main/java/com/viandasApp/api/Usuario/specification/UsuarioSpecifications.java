package com.viandasApp.api.Usuario.specification;

import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.jpa.domain.Specification;

public class UsuarioSpecifications {

    public static Specification<Usuario> nombreContiene(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isBlank()) return null;
            return cb.like(cb.lower(root.get("nombreCompleto")), "%" + nombre.toLowerCase() + "%");
        };
    }

    public static Specification<Usuario> emailContiene(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return null;
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    // Ordenamiento: Activos -> Sin Autenticar -> Baneados -> Eliminados
    public static Specification<Usuario> ordenPorDisponibilidad() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {

                var caseStatus = cb.selectCase()
                        .when(cb.isNotNull(root.get("deletedAt")), 3)
                        .when(cb.isNotNull(root.get("bannedAt")), 2)
                        .when(cb.isFalse(root.get("enabled")), 1)
                        .otherwise(0);

                query.orderBy(
                        cb.asc(caseStatus),
                        cb.asc(root.get("nombreCompleto"))
                );
            }
            return null;
        };
    }

}
