package com.viandasApp.api.Vianda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoriaVianda {
    MENU_DEL_DIA("Menú del día"),
    MILANESA("Milanesas"),
    PASTA("Pastas"),
    PARRILLA("Parrilla"),
    WOK("Woks y salteados"),
    TARTA("Tartas"),
    EMPANADA("Empanadas"),
    ENSALADA("Ensaladas"),
    SOPA("Sopas"),
    PARA_COMPARTIR("Para compartir"),
    SUSHI("Sushi"),
    ARABE("Comida arabe"),
    JAPONESA("Comida japonesa"),
    CHINA("Comida china"),
    PESCADO("Pescados y mariscos"),
    HAMBURGUESA("Hamburguesas"),
    POSTRE("Postres"),
    PIZZA("Pizzas"),
    SANDWICH("Sandwiches");

    private final String descripcion;

    CategoriaVianda(String descripcion) {
        this.descripcion = descripcion;
    }

    @JsonValue
    public String getDescripcion() {
        return descripcion;
    }

    @JsonCreator
    public static CategoriaVianda fromDescripcion(String descripcion) {
        for (CategoriaVianda categoria : values()) {
            if (categoria.getDescripcion().equalsIgnoreCase(descripcion)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoría inválida");
    }
}
