package com.codenzic.employee.dto.request;

import com.codenzic.employee.entity.enums.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to update an existing employee")
public record EmployeeUpdateRequest(

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
        LocalDate joiningDate,

        @Schema(description = "Employee's current status", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Employee status is required")
        EmployeeStatus status

) {
}