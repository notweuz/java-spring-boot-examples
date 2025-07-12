package ru.ntwz.javaspringbootauthexample.service;

public interface BCryptService {

    String getHash(String password);

    boolean verify(String password, String hash);
}
