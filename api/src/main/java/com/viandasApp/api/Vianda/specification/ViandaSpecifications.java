package com.viandasApp.api.Vianda.specification;

import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import org.springframework.data.jpa.domain.Specification;

public class ViandaSpecifications {

    public static Specification<Vianda> estaDisponible() {
        return (root, query, cb) -> cb.isTrue(root.get("estaDisponible"));
    }

    public static Specification<Vianda> estaDisponible(boolean disponible) {
        return (root, query, cb) -> cb.equal(root.get("estaDisponible"), disponible);
    }

    public static Specification<Vianda> perteneceAEmprendimiento(Long emprendimientoId) {
        return (root, query, cb) -> emprendimientoId == null ? null : cb.equal(root.get("emprendimiento").get("id"), emprendimientoId);
    }

    public static Specification<Vianda> esVegana(Boolean vegana) {
        return (root, query, cb) -> vegana == null ? null : cb.equal(root.get("esVegano"), vegana);
    }

    public static Specification<Vianda> esVegetariana(Boolean vegetariana) {
        return (root, query, cb) -> vegetariana == null ? null : cb.equal(root.get("esVegetariano"), vegetariana);
    }

    public static Specification<Vianda> esSinTacc(Boolean sinTacc) {
        return (root, query, cb) -> sinTacc == null ? null : cb.equal(root.get("esSinTacc"), sinTacc);
    }

    public static Specification<Vianda> tieneCategoria(CategoriaVianda categoria) {
        return (root, query, cb) -> categoria == null ? null : cb.equal(root.get("categoria"), categoria);
    }

    public static Specification<Vianda> precioMayorA(Double precioMin) {
        return (root, query, cb) -> precioMin == null ? null : cb.greaterThanOrEqualTo(root.get("precio"), precioMin);
    }

    public static Specification<Vianda> precioMenorA(Double precioMax) {
        return (root, query, cb) -> precioMax == null ? null : cb.lessThanOrEqualTo(root.get("precio"), precioMax);
    }

    public static Specification<Vianda> nombreContieneIgnoreCase(String nombre) {
        return (root, query, cb) -> nombre == null ? null : cb.like(cb.lower(root.get("nombreVianda")), "%" + nombre.toLowerCase() + "%");
    }
}
