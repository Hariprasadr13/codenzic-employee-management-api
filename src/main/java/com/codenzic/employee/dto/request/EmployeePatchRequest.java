package com.codenzic.employee.dto.request;

import com.codenzic.employee.entity.enums.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Optional fields for a partial employee update")
public record EmployeePatchRequest(

        @Schema(description = "Employee's first name")
        @Size(min = 1, max = 100, message = "First name must contain 1 to 100 characters")
        String firstName,

        @Schema(description = "Employee's last name")
        @Size(min = 1, max = 100, message = "Last name must contain 1 to 100 characters")
        String lastName,

        @Schema(description = "Unique employee email address")
        @Email(message = "Please provide a valid email address")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @Schema(description = "Employee's department")
        @Size(max = 100, message = "Department cannot exceed 100 characters")
        String department,

        @Schema(description = "Employee's salary")
        @PositiveOrZero(message = "Salary cannot be negative")
        BigDecimal salary,

        @Schema(description = "Employee's joining date")
        @PastOrPresent(message = "Joining date cannot be in the future")
        LocalDate joiningDate,

        @Schema(description = "Employee status (ACTIVE or INACTIVE)")
        EmployeeStatus status

) {
        public boolean hasUpdates() {
                return firstName != null
                        || lastName != null
                        || email != null
                        || department != null
                        || salary != null
                        || joiningDate != null
                        || status != null;
        }
}