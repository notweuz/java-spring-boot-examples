package ru.ntwz.javaspringbootauthexample.exception;

public class UserWithSameNameAlreadyExistsException extends RuntimeException {
    public UserWithSameNameAlreadyExistsException(String message) {
        super(message);
    }
}
