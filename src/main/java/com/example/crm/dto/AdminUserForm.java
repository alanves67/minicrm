package com.example.crm.dto;

import com.example.crm.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserForm {
    private Long id;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен быть минимум 6 символов")
    private String password;

    @NotNull(message = "Роль обязательна")
    private UserRole role;

    private boolean enabled = true;
}
