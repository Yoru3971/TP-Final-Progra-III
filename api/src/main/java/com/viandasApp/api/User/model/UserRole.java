package com.viandasApp.api.User.model;

public enum UserRole {
    ADMIN("ADMIN"),
    OWNER("OWNER"),
    CLIENT("CLIENT");

    private final String dbIdentifier;

    UserRole(String dbIdentifier) {
        this.dbIdentifier = dbIdentifier;
    }
}
