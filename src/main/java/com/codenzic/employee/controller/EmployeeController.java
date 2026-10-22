package com.codenzic.employee.controller;

import com.codenzic.employee.dto.request.EmployeeCreateRequest;
import com.codenzic.employee.dto.request.EmployeePatchRequest;
import com.codenzic.employee.dto.request.EmployeeSearchRequest;
import com.codenzic.employee.dto.request.EmployeeUpdateRequest;
import com.codenzic.employee.dto.response.ApiErrorResponse;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.dto.response.PageResponse;
import com.codenzic.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Tag(name = "Employees", description = "Employee management operations")
@Validated
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create an employee", description = "Creates a new employee after validating the request.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully", content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid employee data", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Employee email already exists", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        URI location = URI.create("/api/v1/employees/" + response.id());
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "Get employees", description = "Returns paginated employees with optional keyword search and status filtering.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination, sorting, or status parameter")
    })
    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> getAllEmployees(@Valid @ModelAttribute EmployeeSearchRequest request) {
        return ResponseEntity.ok(employeeService.getAllEmployees(request));
    }

    @Operation(summary = "Get employee by ID", description = "Retrieves a single employee using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable("id") Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update employee", description = "Replaces an employee's editable details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employee data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Email conflicts with another employee")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable("id") Long id, @Valid @RequestBody EmployeeUpdateRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Partially update employee", description = "Updates only the supplied employee fields.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employee data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Email conflicts with another employee")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponse> patchEmployee(@PathVariable("id") Long id, @Valid @RequestBody EmployeePatchRequest request) {
        EmployeeResponse response = employeeService.patchEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete employee", description = "Deletes an employee using their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        Map<String, String> response = Map.of("message", "Employee deleted successfully");
        return ResponseEntity.ok(response);
    }
}

