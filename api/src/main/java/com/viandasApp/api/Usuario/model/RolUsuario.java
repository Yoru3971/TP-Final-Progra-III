package com.viandasApp.api.Usuario.model;

public enum RolUsuario {
    ADMIN("ADMIN"),
    OWNER("OWNER"),
    CLIENT("CLIENT");

    private final String dbIdentifier;

    RolUsuario(String dbIdentifier) {
        this.dbIdentifier = dbIdentifier;
    }
}
