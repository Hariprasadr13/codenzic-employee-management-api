package com.codenzic.employee.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Employee details returned by the API")
public record EmployeeResponse(

        @Schema(description = "Unique employee ID", example = "1")
        Long id,

        @Schema(description = "Employee's first name", example = "TestFirstName")
        String firstName,

        @Schema(description = "Employee's last name", example = "TestLastName")
        String lastName,

        @Schema(description = "Employee's email address", example = "test@test.com")
        String email,

        @Schema(description = "Employee's department", example = "TestDepartment")
        String department,

        @Schema(description = "Employee's salary", example = "10000.00")
        BigDecimal salary,

        @Schema(description = "Employee's joining date", example = "YYYY-MM-DD")
        LocalDate joiningDate

) {
}