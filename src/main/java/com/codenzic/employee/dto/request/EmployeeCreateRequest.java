package com.codenzic.employee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to create a new employee")
public record EmployeeCreateRequest(

        @Schema(description = "Employee's first name", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @Schema(description = "Employee's last name", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @Schema(description = "Unique employee email address", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @Schema(description = "Employee's department; optional")
        @Size(max = 100, message = "Department cannot exceed 100 characters")
        String department,

        @Schema(description = "Employee's salary; must be zero or greater", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Salary is required")
        @PositiveOrZero(message = "Salary cannot be negative")
        BigDecimal salary,

        @Schema(description = "Employee's joining date; cannot be in the future", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Joining date is required")
        @PastOrPresent(message = "Joining date cannot be in the future")
        LocalDate joiningDate

) {
}