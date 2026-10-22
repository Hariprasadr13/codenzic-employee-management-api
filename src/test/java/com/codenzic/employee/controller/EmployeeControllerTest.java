package com.codenzic.employee.controller;

import com.codenzic.employee.dto.request.*;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.dto.response.PageResponse;
import com.codenzic.employee.entity.enums.EmployeeStatus;
import com.codenzic.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    @Mock EmployeeService service;
    @InjectMocks EmployeeController controller;

    private EmployeeResponse response() {
        return new EmployeeResponse(1L, "Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("90000"), LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE);
    }

    @Test
    void createEmployeeReturns201AndLocation() {
        EmployeeCreateRequest request = new EmployeeCreateRequest("Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("90000"), LocalDate.of(2020, 1, 1));
        when(service.createEmployee(request)).thenReturn(response());
        var result = controller.createEmployee(request);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("/api/v1/employees/1", result.getHeaders().getLocation().toString());
        assertEquals(response(), result.getBody());
    }

    @Test
    void getAllEmployeesReturns200() {
        EmployeeSearchRequest request = new EmployeeSearchRequest(null, null, null, null, null, null);
        PageResponse<EmployeeResponse> page = new PageResponse<>(List.of(response()), 0, 10, 1, 1, true, true);
        when(service.getAllEmployees(request)).thenReturn(page);
        var result = controller.getAllEmployees(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(page, result.getBody());
    }

    @Test
    void getEmployeeByIdReturns200() {
        when(service.getEmployeeById(1L)).thenReturn(response());
        var result = controller.getEmployeeById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response(), result.getBody());
    }

    @Test
    void updateEmployeeReturns200() {
        EmployeeUpdateRequest request = new EmployeeUpdateRequest("Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("90000"), LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE);
        when(service.updateEmployee(1L, request)).thenReturn(response());
        var result = controller.updateEmployee(1L, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response(), result.getBody());
    }

    @Test
    void patchEmployeeReturns200() {
        EmployeePatchRequest request = new EmployeePatchRequest("Ada", null, null, null, null, null, null);
        when(service.patchEmployee(1L, request)).thenReturn(response());
        var result = controller.patchEmployee(1L, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response(), result.getBody());
    }

    @Test
    void deleteEmployeeReturnsSuccessMessage() {
        doNothing().when(service).deleteEmployee(1L);
        var result = controller.deleteEmployee(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(Map.of("message", "Employee deleted successfully"), result.getBody());
        verify(service).deleteEmployee(1L);
    }
}
