package com.codenzic.employee.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard API error response")
public record ApiErrorResponse(

        @Schema(description = "Timestamp when the error occurred")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "HTTP error reason", example = "Bad Request")
        String error,

        @Schema(description = "Details about the error", example = "Invalid employee data")
        String message,

        @Schema(description = "Request path that caused the error", example = "/api/v1/employees")
        String path,

        @Schema(description = "Field-specific validation errors", example = "{\"email\":\"Please provide a valid email address\"}")
        Map<String, String> fieldErrors

) {
}