package ru.ntwz.javaspringbootauthexample.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ntwz.javaspringbootauthexample.constant.ValidationConstant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInDto {
    @Size(min = ValidationConstant.Length.NAME_MIN, max = ValidationConstant.Length.NAME_MAX,
            message = "Username" + ValidationConstant.Message.WRONG_LENGTH)
    @NotNull(message = "Username" + ValidationConstant.Message.REQUIRED)
    @NotBlank(message = "Username" + ValidationConstant.Message.CAN_NOT_BE_BLANK)
    private String username;

    @Size(min = ValidationConstant.Length.PASSWORD_MIN, max = ValidationConstant.Length.PASSWORD_MAX,
            message = "Password" + ValidationConstant.Message.WRONG_LENGTH)
    @NotNull(message = "Password" + ValidationConstant.Message.REQUIRED)
    @NotBlank(message = "Username" + ValidationConstant.Message.CAN_NOT_BE_BLANK)
    private String password;
}
