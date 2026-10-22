package com.codenzic.employee.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.util.Locale;

public record PaginationRequest(

        @Min(value = 0, message = "Page cannot be negative")
        Integer page,

        @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size cannot exceed 100")
        Integer size,

        String sortBy,

        @Pattern(regexp = "(?i)asc|desc", message = "Direction must be asc or desc")
        String direction

) {
    public PaginationRequest {
        page = Math.max(page, 0);
        size = size <= 0 ? 10 : Math.min(size, 100);
        sortBy = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy.trim();
        direction = (direction == null || direction.isBlank()) ? "asc" : direction.trim().toLowerCase(Locale.ROOT);
    }
}
