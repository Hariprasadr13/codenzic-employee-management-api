package com.codenzic.employee.dto.mapper;

import com.codenzic.employee.dto.request.EmployeeCreateRequest;
import com.codenzic.employee.dto.request.EmployeePatchRequest;
import com.codenzic.employee.dto.request.EmployeeUpdateRequest;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.entity.Employee;
import com.codenzic.employee.entity.enums.EmployeeStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {
    private final EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    void mapsCreateRequestToEntityAndIgnoresId() {
        var request = new EmployeeCreateRequest("Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("1000.00"), LocalDate.of(2020, 1, 1));
        Employee employee = mapper.toEntity(request);
        assertNull(employee.getId());
        assertEquals("Ada", employee.getFirstName());
        assertEquals("Lovelace", employee.getLastName());
        assertEquals("ada@example.com", employee.getEmail());
        assertEquals("Engineering", employee.getDepartment());
        assertEquals(new BigDecimal("1000.00"), employee.getSalary());
        assertEquals(LocalDate.of(2020, 1, 1), employee.getJoiningDate());
    }

    @Test
    void updatesEntityFieldsWithoutChangingId() {
        Employee employee = sampleEmployee();
        var request = new EmployeeUpdateRequest("Grace", "Hopper", "grace@example.com", "Research", new BigDecimal("2000"), LocalDate.of(2021, 2, 2), EmployeeStatus.ACTIVE);
        mapper.updateEntity(request, employee);
        assertEquals(5L, employee.getId());
        assertEquals("Grace", employee.getFirstName());
        assertEquals("grace@example.com", employee.getEmail());
        assertEquals("Research", employee.getDepartment());
    }

    @Test
    void patchIgnoresNullValuesAndPreservesId() {
        Employee employee = sampleEmployee();
        var patch = new EmployeePatchRequest("Grace", null, null, null, null, null, null);
        mapper.applyPatch(patch, employee);
        assertEquals(5L, employee.getId());
        assertEquals("Grace", employee.getFirstName());
        assertEquals("Lovelace", employee.getLastName());
        assertEquals("ada@example.com", employee.getEmail());
        assertEquals("Engineering", employee.getDepartment());
    }

    @Test
    void mapsEntityToResponse() {
        EmployeeResponse response = mapper.toResponse(sampleEmployee());
        assertEquals(5L, response.id());
        assertEquals("Ada", response.firstName());
        assertEquals("ada@example.com", response.email());
        assertEquals(new BigDecimal("1000.00"), response.salary());
    }

    private Employee sampleEmployee() {
        Employee employee = new Employee();
        employee.setId(5L);
        employee.setFirstName("Ada");
        employee.setLastName("Lovelace");
        employee.setEmail("ada@example.com");
        employee.setDepartment("Engineering");
        employee.setSalary(new BigDecimal("1000.00"));
        employee.setJoiningDate(LocalDate.of(2020, 1, 1));
        return employee;
    }
}
