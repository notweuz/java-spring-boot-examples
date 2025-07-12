package ru.ntwz.javaspringbootauthexample.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ntwz.javaspringbootauthexample.dto.request.SignInDto;
import ru.ntwz.javaspringbootauthexample.dto.request.SignUpDto;
import ru.ntwz.javaspringbootauthexample.dto.response.AuthTokenDto;
import ru.ntwz.javaspringbootauthexample.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public AuthTokenDto signup(
            @RequestBody @Valid SignUpDto signUpDto
    ) {
        log.info("Received signup request for username: {}", signUpDto.getUsername());
        return authenticationService.singUp(signUpDto);
    }

    @PostMapping("/signin")
    public AuthTokenDto signin(
            @RequestBody @Valid SignInDto signInDto
    ) {
        log.info("Received sign in request for username: {}", signInDto.getUsername());
        return authenticationService.signIn(signInDto);
    }
}
