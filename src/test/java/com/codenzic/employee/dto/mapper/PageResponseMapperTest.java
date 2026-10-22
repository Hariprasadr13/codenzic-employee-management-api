package com.codenzic.employee.dto.mapper;

import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.dto.response.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseMapperTest {
    @Test
    void mapsContentAndPageMetadata() {
        var source = new PageImpl<>(List.of("a", "b"), PageRequest.of(0, 2), 2);
        PageResponse<String> result = PageResponseMapper.toResponse(source, String::toUpperCase);
        assertEquals(List.of("A", "B"), result.content());
        assertEquals(0, result.page());
        assertEquals(2, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.first());
        assertTrue(result.last());
    }

    @Test
    void mapsEmptyPage() {
        var source = new PageImpl<String>(List.of(), PageRequest.of(0, 10), 0);
        PageResponse<String> result = PageResponseMapper.toResponse(source, Function.identity());
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertTrue(result.first());
        assertTrue(result.last());
    }
}
