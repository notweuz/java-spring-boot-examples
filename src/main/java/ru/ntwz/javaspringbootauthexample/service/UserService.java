package ru.ntwz.javaspringbootauthexample.service;

import ru.ntwz.javaspringbootauthexample.dto.response.UserDto;
import ru.ntwz.javaspringbootauthexample.model.User;
import ru.ntwz.javaspringbootauthexample.security.CustomUserDetailsService;

public interface UserService {

    void create(User user);

    User getByUsername(String username);

    CustomUserDetailsService userDetailsService();

    User getCurrentUser();

    // Methods that return Dto

    UserDto getCurrentUserInfo();
}
