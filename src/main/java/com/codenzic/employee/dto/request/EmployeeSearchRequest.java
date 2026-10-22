package com.codenzic.employee.dto.request;

import com.codenzic.employee.entity.enums.EmployeeStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeSearchRequest(

        @Min(0)
        Integer page,

        @Min(1)
        @Max(100)
        Integer size,

        @Size(max = 100)
        String search,

        EmployeeStatus status,

        @Pattern(regexp = "id|firstName|lastName|email|department|salary|joiningDate")
        String sortBy,

        @Pattern(regexp = "(?i)asc|desc")
        String direction

) {
    public EmployeeSearchRequest {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        sortBy = sortBy == null ? "id" : sortBy;
        direction = direction == null ? "asc" : direction;
    }

    public PaginationRequest toPaginationRequest() {
        return new PaginationRequest(page, size, sortBy, direction);
    }
}