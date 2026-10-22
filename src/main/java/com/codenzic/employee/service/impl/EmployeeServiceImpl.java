package com.codenzic.employee.service.impl;

import com.codenzic.employee.dto.mapper.EmployeeMapper;
import com.codenzic.employee.dto.mapper.PageResponseMapper;
import com.codenzic.employee.dto.request.EmployeeCreateRequest;
import com.codenzic.employee.dto.request.EmployeePatchRequest;
import com.codenzic.employee.dto.request.EmployeeSearchRequest;
import com.codenzic.employee.dto.request.EmployeeUpdateRequest;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.dto.response.PageResponse;
import com.codenzic.employee.entity.Employee;
import com.codenzic.employee.exception.DuplicateEmployeeEmailException;
import com.codenzic.employee.exception.EmployeeNotFoundException;
import com.codenzic.employee.repository.EmployeeRepository;
import com.codenzic.employee.repository.specification.EmployeeSpecification;
import com.codenzic.employee.service.EmployeeService;
import com.codenzic.employee.utils.PaginationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmployeeEmailException(request.email());
        }
        Employee employee = employeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = findEmployeeOrThrow(id);
        validateEmailUnique(request.email(), id);
        employeeMapper.updateEntity(request, employee);
        return saveAndMap(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse patchEmployee(Long id, EmployeePatchRequest request) {
        if (!request.hasUpdates()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        Employee employee = findEmployeeOrThrow(id);
        if (request.email() != null) {
            validateEmailUnique(request.email(), id);
        }
        employeeMapper.applyPatch(request, employee);
        return saveAndMap(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAllEmployees(EmployeeSearchRequest request) {
        var pageable = PaginationUtils.createPageable(request.toPaginationRequest());
        var specification = EmployeeSpecification.searchAndFilter(request.search(), request.status());
        return PageResponseMapper.toResponse(
                employeeRepository.findAll(specification, pageable),
                employeeMapper::toResponse
        );
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private void validateEmailUnique(String email, Long employeeId) {
        if (employeeRepository.existsByEmailAndIdNot(email, employeeId)) {
            throw new DuplicateEmployeeEmailException(email);
        }
    }

    private EmployeeResponse saveAndMap(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }
}