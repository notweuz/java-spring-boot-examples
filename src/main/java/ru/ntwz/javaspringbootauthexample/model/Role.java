package ru.ntwz.javaspringbootauthexample.model;

public enum Role {
    USER;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
