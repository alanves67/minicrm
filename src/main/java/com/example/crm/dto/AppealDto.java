package com.example.crm.dto;

import com.example.crm.model.AppealStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppealDto {
    private Long id;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    private AppealStatus status;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}