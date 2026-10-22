package com.codenzic.employee.repository.specification;

import com.codenzic.employee.entity.Employee;
import com.codenzic.employee.entity.enums.EmployeeStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
class EmployeeSpecificationTest {
    @Test
    void noSearchAndNoStatusReturnsConjunction() {
        Root<Employee> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conjunction = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conjunction);

        Predicate result = EmployeeSpecification.searchAndFilter("  ", null).toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(root);
    }


    @Test
    void searchAndStatusBuildCombinedPredicate() {
        Root<Employee> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<String> firstNamePath = mock(Path.class);
        Path<String> lastNamePath = mock(Path.class);
        Path<String> emailPath = mock(Path.class);
        Path<EmployeeStatus> statusPath = mock(Path.class);

        Predicate conjunction = mock(Predicate.class);
        Predicate firstNameLike = mock(Predicate.class);
        Predicate lastNameLike = mock(Predicate.class);
        Predicate emailLike = mock(Predicate.class);
        Predicate searchPredicate = mock(Predicate.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(cb.conjunction()).thenReturn(conjunction);

        when(root.<String>get("firstName")).thenReturn(firstNamePath);
        when(root.<String>get("lastName")).thenReturn(lastNamePath);
        when(root.<String>get("email")).thenReturn(emailPath);
        when(root.<EmployeeStatus>get("status")).thenReturn(statusPath);

        when(cb.lower(firstNamePath)).thenReturn(firstNamePath);
        when(cb.lower(lastNamePath)).thenReturn(lastNamePath);
        when(cb.lower(emailPath)).thenReturn(emailPath);

        when(cb.like(firstNamePath, "%ada%")).thenReturn(firstNameLike);
        when(cb.like(lastNamePath, "%ada%")).thenReturn(lastNameLike);
        when(cb.like(emailPath, "%ada%")).thenReturn(emailLike);

        when(cb.or(firstNameLike, lastNameLike, emailLike))
                .thenReturn(searchPredicate);

        when(cb.equal(statusPath, EmployeeStatus.ACTIVE))
                .thenReturn(statusPredicate);

        when(cb.and(conjunction, searchPredicate))
                .thenReturn(combinedPredicate);

        when(cb.and(combinedPredicate, statusPredicate))
                .thenReturn(combinedPredicate);

        Specification<Employee> specification =
                EmployeeSpecification.searchAndFilter(
                        "  ADA ",
                        EmployeeStatus.ACTIVE
                );

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertSame(combinedPredicate, result);

        verify(cb).conjunction();
        verify(cb).or(firstNameLike, lastNameLike, emailLike);
        verify(cb).equal(statusPath, EmployeeStatus.ACTIVE);
        verify(cb).and(conjunction, searchPredicate);
        verify(cb).and(combinedPredicate, statusPredicate);
    }

}
