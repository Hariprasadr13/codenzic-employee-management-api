package com.codenzic.employee.utils;

import com.codenzic.employee.dto.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {

    private PaginationUtils() {
        // Utility class; prevent instantiation.
    }

    public static Pageable createPageable(PaginationRequest request) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.direction());
        Sort sort = Sort.by(sortDirection, request.sortBy());
        return PageRequest.of(request.page(), request.size(), sort);
    }
}