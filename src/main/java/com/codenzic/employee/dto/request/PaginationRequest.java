package com.codenzic.employee.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record PaginationRequest(

        @Min(value = 0, message = "Page cannot be negative")
        Integer page,

        @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size cannot exceed 100")
        Integer size,

        @Pattern(regexp = "id|firstName|lastName|email|department|salary|joiningDate", message = "Unsupported sort field")
        String sortBy,

        @Pattern(regexp = "(?i)asc|desc", message = "Direction must be asc or desc")
        String direction

) {
    public PaginationRequest {
        page = Math.max(page, 0);
        size = size <= 0 ? 10 : Math.min(size, 100);
        sortBy = (sortBy == null || sortBy.isBlank())
                ? "id"
                : sortBy;
        direction = (direction == null || direction.isBlank())
                ? "asc"
                : direction;
    }
}
