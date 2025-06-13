package com.viandasApp.api.User.model;

public enum RolUsuario {
    ADMIN("ADMIN"),
    OWNER("OWNER"),
    CLIENT("CLIENT");

    private final String dbIdentifier;

    RolUsuario(String dbIdentifier) {
        this.dbIdentifier = dbIdentifier;
    }
}
