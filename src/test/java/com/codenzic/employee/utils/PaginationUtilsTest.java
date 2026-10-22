package com.codenzic.employee.utils;

import com.codenzic.employee.dto.request.PaginationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class PaginationUtilsTest {
    @Test
    void createsPageableWithAscendingSort() {
        var pageable = PaginationUtils.createPageable(new PaginationRequest(2, 25, "email", "asc"));
        assertEquals(2, pageable.getPageNumber());
        assertEquals(25, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("email").getDirection());
    }

    @Test
    void createsPageableWithDescendingSort() {
        var pageable = PaginationUtils.createPageable(new PaginationRequest(0, 10, "salary", "desc"));
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("salary").getDirection());
    }

    @Test
    void rejectsUnsupportedDirection() {
        assertThrows(IllegalArgumentException.class,
                () -> PaginationUtils.createPageable(new PaginationRequest(0, 10, "id", "sideways")));
    }
}
