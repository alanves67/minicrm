package com.example.crm.service;

import java.util.Set;

public final class PaginationValidator {

    private PaginationValidator() {
    }

    public static void validate(int page, int size, String sortBy, String sortDir, Set<String> allowedSortFields) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("sortBy must be one of: " + String.join(", ", allowedSortFields));
        }
        if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
            throw new IllegalArgumentException("sortDir must be either 'asc' or 'desc'");
        }
    }
}
