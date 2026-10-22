package com.codenzic.employee.dto.mapper;

import com.codenzic.employee.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    public static <S, T> PageResponse<T> toResponse(Page<S> page, Function<S, T> mapper) {
        var content = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}

