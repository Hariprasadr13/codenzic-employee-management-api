package com.codenzic.employee.dto.request;

import com.codenzic.employee.entity.enums.EmployeeStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRequestValidationTest {
    private static Validator validator;
    private static jakarta.validation.ValidatorFactory factory;

    @BeforeAll
    static void createValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void validCreateRequestHasNoViolations() {
        var request = new EmployeeCreateRequest("Ada", "Lovelace", "ada@example.com", "Engineering", new BigDecimal("50000"), LocalDate.now());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void invalidCreateRequestReportsRequiredEmailAndSalary() {
        var request = new EmployeeCreateRequest(" ", "Lovelace", "not-an-email", null, new BigDecimal("-1"), LocalDate.now().plusDays(1));
        Set<String> properties = validator.validate(request).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(properties.contains("firstName"));
        assertTrue(properties.contains("email"));
        assertTrue(properties.contains("salary"));
        assertTrue(properties.contains("joiningDate"));
    }

    @Test
    void invalidUpdateRequestIsRejected() {
        var request = new EmployeeUpdateRequest("", "Lovelace", "bad-email", null, null, null, null);
        Set<String> properties = validator.validate(request).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(properties.contains("firstName"));
        assertTrue(properties.contains("email"));
        assertTrue(properties.contains("salary"));
        assertTrue(properties.contains("joiningDate"));
    }

    @Test
    void patchRequestAllowsNullFieldsButValidatesProvidedValues() {
        var valid = new EmployeePatchRequest("Ada", null, null, null, null, null, null);
        assertTrue(validator.validate(valid).isEmpty());

        var invalid = new EmployeePatchRequest("", null, "bad-email", null, new BigDecimal("-10"), LocalDate.now().plusDays(1), EmployeeStatus.ACTIVE);
        Set<String> properties = validator.validate(invalid).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(properties.contains("firstName"));
        assertTrue(properties.contains("email"));
        assertTrue(properties.contains("salary"));
        assertTrue(properties.contains("joiningDate"));
    }

    @Test
    void searchRequestAppliesDefaultsAndConvertsPagination() {
        var request = new EmployeeSearchRequest(null, null, null, EmployeeStatus.ACTIVE, null, null);
        assertEquals(0, request.page());
        assertEquals(10, request.size());
        assertEquals("id", request.sortBy());
        assertEquals("asc", request.direction());
        assertEquals(0, request.toPaginationRequest().page());
        assertEquals(10, request.toPaginationRequest().size());
    }

    @Test
    void searchRequestValidationRejectsInvalidPageSizeSortAndDirection() {
        var request = new EmployeeSearchRequest(-1, 101, "x".repeat(101), null, "password", "sideways");
        Set<String> properties = validator.validate(request).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(properties.contains("page"));
        assertTrue(properties.contains("size"));
        assertTrue(properties.contains("search"));
        assertTrue(properties.contains("sortBy"));
        assertTrue(properties.contains("direction"));
    }

    @Test
    void paginationRequestNormalizesUnsafeNumbersAndBlankSortValues() {
        var request = new PaginationRequest(-4, 0, " ", " ");
        assertEquals(0, request.page());
        assertEquals(10, request.size());
        assertEquals("id", request.sortBy());
        assertEquals("asc", request.direction());

        var oversized = new PaginationRequest(0, 500, "salary", "desc");
        assertEquals(100, oversized.size());
    }
}
