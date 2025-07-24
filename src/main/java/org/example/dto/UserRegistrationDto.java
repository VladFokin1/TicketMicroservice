package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.dto.validator.ValidRole;
import org.example.model.Role;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    private String login;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, буквы и цифры")
    private String password;

    @NotBlank(message = "ФИО обязательно")
    @Size(min = 5, max = 100, message = "ФИО должно быть от 5 до 100 символов")
    private String fullName;

    @NotNull(message = "Роль обязательна")
    @ValidRole(message = "Допустимые роли: ROLE_CUSTOMER или ROLE_ADMIN")
    private Role role;
}
