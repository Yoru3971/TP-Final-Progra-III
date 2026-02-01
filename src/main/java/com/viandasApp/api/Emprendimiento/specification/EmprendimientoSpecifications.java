package com.viandasApp.api.Emprendimiento.specification;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class EmprendimientoSpecifications {

    public static Specification<Emprendimiento> porCiudad(String ciudad) {
        return (root, query, cb) -> {
            if (ciudad == null || ciudad.isBlank()) return null;
            return cb.like(cb.lower(root.get("ciudad")), "%" + ciudad.toLowerCase() + "%");
        };
    }

    public static Specification<Emprendimiento> porNombre(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isBlank()) return null;
            return cb.like(cb.lower(root.get("nombreEmprendimiento")), "%" + nombre.toLowerCase() + "%");
        };
    }

    public static Specification<Emprendimiento> estaDisponible(boolean disponible) {
        return (root, query, cb) -> cb.equal(root.get("estaDisponible"), disponible);
    }

    public static Specification<Emprendimiento> noEstaEliminado() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Emprendimiento> perteneceADueno(Long duenoId) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("id"), duenoId);
    }

    // Filtro por nombre o email del dueño (JOIN)
    public static Specification<Emprendimiento> duenoNombreOEmailContiene(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) return null;
            String textoLike = "%" + texto.toLowerCase() + "%";

            Join<Emprendimiento, Usuario> dueno = root.join("usuario");

            return cb.or(
                    cb.like(cb.lower(dueno.get("nombre")), textoLike),
                    cb.like(cb.lower(dueno.get("apellido")), textoLike),
                    cb.like(cb.lower(dueno.get("email")), textoLike)
            );
        };
    }

    // Ordenamiento para admin: Disponibles -> No Disponibles -> Eliminados
    public static Specification<Emprendimiento> ordenamientoAdmin() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {

                var caseStatus = cb.selectCase()
                        .when(cb.and(cb.isNull(root.get("deletedAt")), cb.isTrue(root.get("estaDisponible"))), 0)
                        .when(cb.and(cb.isNull(root.get("deletedAt")), cb.isFalse(root.get("estaDisponible"))), 1)
                        .otherwise(2);

                query.orderBy(
                        cb.asc(caseStatus),
                        cb.asc(root.get("nombreEmprendimiento"))
                );
            }
            return null;
        };
    }

    // Filtro solo los que tienen fecha de baja
    public static Specification<Emprendimiento> soloEliminados(boolean mostrarSoloEliminados) {
        return (root, query, cb) -> {
            if (!mostrarSoloEliminados) return null;
            return cb.isNotNull(root.get("deletedAt"));
        };
    }
}
