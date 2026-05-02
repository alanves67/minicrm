package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserPasswordForm {
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен быть минимум 6 символов")
    private String password;
}
