package ru.ntwz.javaspringbootauthexample.service;

import ru.ntwz.javaspringbootauthexample.dto.request.SignInDto;
import ru.ntwz.javaspringbootauthexample.dto.request.SignUpDto;
import ru.ntwz.javaspringbootauthexample.dto.response.AuthTokenDto;

public interface AuthenticationService {

    AuthTokenDto singUp(SignUpDto signUpDto);

    AuthTokenDto signIn(SignInDto signInDto);
}
