package ru.ntwz.javaspringbootauthexample.dto.mapper;

import lombok.NonNull;
import ru.ntwz.javaspringbootauthexample.dto.request.SignUpDto;
import ru.ntwz.javaspringbootauthexample.dto.response.UserDto;
import ru.ntwz.javaspringbootauthexample.model.User;

public class UserMapper {
    public static User userFromSignUpDto(@NonNull SignUpDto signUpDto) {
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setPassword(signUpDto.getPassword());
        return user;
    }

    public static UserDto userDtoFromUser(@NonNull User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        return userDto;
    }
}
