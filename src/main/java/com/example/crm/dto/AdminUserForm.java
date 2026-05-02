package com.example.crm.dto;

import com.example.crm.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserForm {
    private Long id;

    @NotBlank(message = "Логин обязателен")
    @jakarta.validation.constraints.Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    private String username;

    private String password;

    @NotNull(message = "Роль обязательна")
    private UserRole role;

    private boolean enabled = true;
}
