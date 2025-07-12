package ru.ntwz.javaspringbootauthexample.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ntwz.javaspringbootauthexample.dto.mapper.UserMapper;
import ru.ntwz.javaspringbootauthexample.dto.request.SignInDto;
import ru.ntwz.javaspringbootauthexample.dto.request.SignUpDto;
import ru.ntwz.javaspringbootauthexample.dto.response.AuthTokenDto;
import ru.ntwz.javaspringbootauthexample.exception.InvalidPasswordException;
import ru.ntwz.javaspringbootauthexample.model.User;
import ru.ntwz.javaspringbootauthexample.security.BCryptServicePasswordEncoder;
import ru.ntwz.javaspringbootauthexample.service.AuthenticationService;
import ru.ntwz.javaspringbootauthexample.service.JwtService;
import ru.ntwz.javaspringbootauthexample.service.UserService;

@Service
public class AuthentificationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptServicePasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthentificationServiceImpl(
            UserService userService,
            JwtService jwtService,
            BCryptServicePasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthTokenDto singUp(SignUpDto signUpDto) {
        User user = UserMapper.userFromSignUpDto(signUpDto);
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        userService.create(user);

        String jwt = jwtService.generateToken(user);
        return new AuthTokenDto(jwt);
    }

    @Override
    public AuthTokenDto signIn(SignInDto signInDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInDto.getUsername(),
                    signInDto.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException("Invalid username or password");
        }

        User user = userService.getByUsername(signInDto.getUsername());
        UserDetails details = userService.userDetailsService().loadUserById(user.getId());
        String jwt = jwtService.generateToken(details);
        return new AuthTokenDto(jwt);
    }
}
