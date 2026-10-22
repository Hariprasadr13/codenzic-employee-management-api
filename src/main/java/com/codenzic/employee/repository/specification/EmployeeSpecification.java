package com.codenzic.employee.repository.specification;

import com.codenzic.employee.entity.Employee;
import com.codenzic.employee.entity.enums.EmployeeStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class EmployeeSpecification {

    private EmployeeSpecification() {
    }

    public static Specification<Employee> searchAndFilter(String search, EmployeeStatus status) {

        return (root, query, criteriaBuilder) -> {

            var predicates = criteriaBuilder.conjunction();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";

                var searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern)
                );

                predicates = criteriaBuilder.and(predicates, searchPredicate);
            }

            if (status != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("status"), status));
            }

            return predicates;
        };
    }
}