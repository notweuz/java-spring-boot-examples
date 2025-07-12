package ru.ntwz.javaspringbootauthexample.model;

public enum Role {
    USER,
    ADMIN,
    MODERATOR;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
