package com.codenzic.employee.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/employees");
    }

    @Test
    void handlesNotFound() {
        var result = handler.handleEmployeeNotFound(new EmployeeNotFoundException(9L), request);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("/api/v1/employees", result.getBody().path());
        assertEquals(404, result.getBody().status());
    }

    @Test
    void handlesDuplicateEmail() {
        var result = handler.handleDuplicateEmail(new DuplicateEmployeeEmailException("ada@example.com"), request);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertTrue(result.getBody().message().contains("ada@example.com"));
    }

    @Test
    void handlesFieldValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("request", "email", "Invalid email")));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        var result = handler.handleValidationErrors(exception, request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Request validation failed", result.getBody().message());
        assertEquals("Invalid email", result.getBody().fieldErrors().get("email"));
    }

    @Test
    void handlesUnreadableRequestBody() {
        var result = handler.handleUnreadableBody(new HttpMessageNotReadableException("Malformed JSON", new MockHttpInputMessage(new byte[0])), request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().message().contains("invalid JSON"));
    }

    @Test
    void handlesTypeMismatch() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("status");
        var result = handler.handleTypeMismatch(exception, request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid value for parameter: status", result.getBody().message());
    }

    @Test
    void handlesConstraintViolation() {
        var result = handler.handleConstraintViolation(new ConstraintViolationException("invalid", Set.of()), request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Request constraint validation failed", result.getBody().message());
    }

    @Test
    void handlesDataIntegrityViolation() {
        var result = handler.handleDataIntegrityViolation(new DataIntegrityViolationException("unique constraint"), request);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    void handlesUnexpectedExceptionWithoutExposingInternalMessage() {
        var result = handler.handleUnexpectedException(new IllegalStateException("database password leaked"), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An unexpected error occurred", result.getBody().message());
        assertFalse(result.getBody().message().contains("database password"));
    }

    @Test
    void handlesIllegalArgumentAsBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("At least one field must be provided for update");
        var result = handler.handleIllegalArgument(exception, request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(400, result.getBody().status());
        assertEquals("At least one field must be provided for update", result.getBody().message());
        assertEquals("/api/v1/employees", result.getBody().path());
    }
}
