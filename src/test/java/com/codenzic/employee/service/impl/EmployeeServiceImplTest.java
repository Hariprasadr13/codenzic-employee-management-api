package com.codenzic.employee.service.impl;

import com.codenzic.employee.dto.mapper.EmployeeMapper;
import com.codenzic.employee.dto.request.*;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.dto.response.PageResponse;
import com.codenzic.employee.entity.Employee;
import com.codenzic.employee.entity.enums.EmployeeStatus;
import com.codenzic.employee.exception.DuplicateEmployeeEmailException;
import com.codenzic.employee.exception.EmployeeNotFoundException;
import com.codenzic.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock EmployeeRepository repository;
    @Mock EmployeeMapper mapper;
    @InjectMocks EmployeeServiceImpl service;

    private Employee employee;
    private EmployeeResponse response;
    private EmployeeCreateRequest createRequest;
    private EmployeeUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        employee = employee(1L, "Ada", "Lovelace", "ada@example.com");
        response = response(1L, "Ada", "Lovelace", "ada@example.com");
        createRequest = new EmployeeCreateRequest("Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("90000"), LocalDate.of(2020, 1, 1));
        updateRequest = new EmployeeUpdateRequest("Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("90000"), LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE);
    }

    @Test
    void createEmployeeSavesAndMapsEmployee() {
        when(repository.existsByEmail(createRequest.email())).thenReturn(false);
        when(mapper.toEntity(createRequest)).thenReturn(employee);
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        assertEquals(response, service.createEmployee(createRequest));
        verify(repository).save(employee);
        verify(mapper).toResponse(employee);
    }

    @Test
    void createEmployeeRejectsDuplicateEmail() {
        when(repository.existsByEmail(createRequest.email())).thenReturn(true);
        assertThrows(DuplicateEmployeeEmailException.class, () -> service.createEmployee(createRequest));
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    void getEmployeeByIdReturnsMappedEmployee() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.toResponse(employee)).thenReturn(response);
        assertEquals(response, service.getEmployeeById(1L));
    }

    @Test
    void getEmployeeByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> service.getEmployeeById(99L));
    }

    @Test
    void updateEmployeeUpdatesAndSaves() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.existsByEmailAndIdNot(updateRequest.email(), 1L)).thenReturn(false);
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        assertEquals(response, service.updateEmployee(1L, updateRequest));
        verify(mapper).updateEntity(updateRequest, employee);
        verify(repository).save(employee);
    }

    @Test
    void updateEmployeeRejectsEmailUsedByAnotherEmployee() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.existsByEmailAndIdNot(updateRequest.email(), 1L)).thenReturn(true);
        assertThrows(DuplicateEmployeeEmailException.class, () -> service.updateEmployee(1L, updateRequest));
        verify(repository, never()).save(any());
    }

    @Test
    void updateEmployeeThrowsWhenEmployeeMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> service.updateEmployee(7L, updateRequest));
        verify(repository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void patchEmployeeRejectsEmptyPatch() {
        EmployeePatchRequest patch = new EmployeePatchRequest(null, null, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> service.patchEmployee(1L, patch));
        verifyNoInteractions(repository, mapper);
    }

    @Test
    void patchEmployeeUpdatesWithoutEmailCheckWhenEmailNotProvided() {
        EmployeePatchRequest patch = new EmployeePatchRequest("Augusta", null, null, null, null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        assertEquals(response, service.patchEmployee(1L, patch));
        verify(mapper).applyPatch(patch, employee);
        verify(repository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void patchEmployeeValidatesEmailWhenProvided() {
        EmployeePatchRequest patch = new EmployeePatchRequest(null, null, "new@example.com", null, null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(true);
        assertThrows(DuplicateEmployeeEmailException.class, () -> service.patchEmployee(1L, patch));
        verify(repository, never()).save(any());
    }

    @Test
    void patchEmployeeThrowsWhenEmployeeMissing() {
        EmployeePatchRequest patch = new EmployeePatchRequest("Augusta", null, null, null, null, null, null);
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> service.patchEmployee(5L, patch));
    }

    @Test
    void deleteEmployeeDeletesExistingEmployee() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        service.deleteEmployee(1L);
        verify(repository).delete(employee);
    }

    @Test
    void deleteEmployeeThrowsWhenMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> service.deleteEmployee(1L));
        verify(repository, never()).delete(any(Employee.class));
    }

    @Test
    void getAllEmployeesReturnsMappedPageAndUsesSpecificationAndPageable() {
        EmployeeSearchRequest request = new EmployeeSearchRequest(1, 5, "ada", EmployeeStatus.ACTIVE, "firstName", "desc");
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee), org.springframework.data.domain.PageRequest.of(1, 5), 11));
        when(mapper.toResponse(employee)).thenReturn(response);

        PageResponse<EmployeeResponse> result = service.getAllEmployees(request);

        assertEquals(List.of(response), result.content());
        assertEquals(1, result.page());
        assertEquals(5, result.size());
        assertEquals(11, result.totalElements());
        assertEquals(3, result.totalPages());
        assertFalse(result.first());
        assertFalse(result.last());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals("firstName", pageable.getSort().getOrderFor("firstName").getProperty());
        assertTrue(pageable.getSort().getOrderFor("firstName").isDescending());
    }

    private static Employee employee(Long id, String firstName, String lastName, String email) {
        Employee e = new Employee();
        e.setId(id); e.setFirstName(firstName); e.setLastName(lastName); e.setEmail(email);
        e.setDepartment("Engineering"); e.setSalary(new BigDecimal("90000"));
        e.setJoiningDate(LocalDate.of(2020, 1, 1)); e.setStatus(EmployeeStatus.ACTIVE);
        return e;
    }

    private static EmployeeResponse response(Long id, String firstName, String lastName, String email) {
        return new EmployeeResponse(id, firstName, lastName, email, "Engineering", new BigDecimal("90000"), LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE);
    }
}
