package ru.ntwz.javaspringbootauthexample.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ntwz.javaspringbootauthexample.dto.mapper.UserMapper;
import ru.ntwz.javaspringbootauthexample.dto.request.ChangePasswordDto;
import ru.ntwz.javaspringbootauthexample.dto.response.AuthTokenDto;
import ru.ntwz.javaspringbootauthexample.dto.response.UserDto;
import ru.ntwz.javaspringbootauthexample.exception.InvalidPasswordException;
import ru.ntwz.javaspringbootauthexample.exception.NotFoundException;
import ru.ntwz.javaspringbootauthexample.exception.UserWithSameNameAlreadyExistsException;
import ru.ntwz.javaspringbootauthexample.model.User;
import ru.ntwz.javaspringbootauthexample.repository.UserRepository;
import ru.ntwz.javaspringbootauthexample.security.CustomUserDetailsService;
import ru.ntwz.javaspringbootauthexample.service.BCryptService;
import ru.ntwz.javaspringbootauthexample.service.JwtService;
import ru.ntwz.javaspringbootauthexample.service.UserService;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final BCryptService bCryptService;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService,
            BCryptService bCryptService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.bCryptService = bCryptService;
        this.jwtService = jwtService;
    }

    @Override
    public void create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("User with username '{}' already exists.", user.getUsername());
            throw new UserWithSameNameAlreadyExistsException("User with username '" + user.getUsername() + "' already exists.");
        }

        log.info("Creating new user with username: {}", user.getUsername());

        userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username '" + username + "' not found."));
    }

    @Override
    public CustomUserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            return userRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundException("Current user not found."));
        }
        throw new NotFoundException("Current user not found.");
    }

    @Override
    public UserDto getCurrentUserInfo() {
        User user = getCurrentUser();
        return UserMapper.userDtoFromUser(user);
    }

    @Override
    public AuthTokenDto changePassword(ChangePasswordDto changePasswordDto) {
        String oldPassword = changePasswordDto.getOldPassword();
        String newPassword = changePasswordDto.getNewPassword();

        User user = getCurrentUser();
        if (!bCryptService.verify(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect.");
        }
        user.setPassword(bCryptService.getHash(newPassword));
        userRepository.save(user);
        String newToken = jwtService.generateToken(user);
        log.info("Password changed for user: {}", user.getUsername());
        return new AuthTokenDto(newToken);
    }
}
