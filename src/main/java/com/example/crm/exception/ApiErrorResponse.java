package com.example.crm.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        String path,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
}
