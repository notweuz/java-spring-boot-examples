package ru.ntwz.javaspringbootauthexample.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ntwz.javaspringbootauthexample.dto.request.ChangePasswordDto;
import ru.ntwz.javaspringbootauthexample.dto.response.AuthTokenDto;
import ru.ntwz.javaspringbootauthexample.dto.response.UserDto;
import ru.ntwz.javaspringbootauthexample.service.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/@me")
    public UserDto getCurrentUserInfo() {
        log.info("Received request for current user info");
        return userService.getCurrentUserInfo();
    }

    @PostMapping("/@me/password")
    public AuthTokenDto changePassword(
            @Valid @RequestBody ChangePasswordDto changePasswordDto
    ) {
        log.info("Received request to change password for current user");
        return userService.changePassword(changePasswordDto);
    }
}
