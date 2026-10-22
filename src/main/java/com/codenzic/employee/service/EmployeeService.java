package com.codenzic.employee.service;

import com.codenzic.employee.dto.request.EmployeeCreateRequest;
import com.codenzic.employee.dto.request.EmployeePatchRequest;
import com.codenzic.employee.dto.request.EmployeeSearchRequest;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.dto.request.EmployeeUpdateRequest;
import com.codenzic.employee.dto.response.PageResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);

    EmployeeResponse patchEmployee(Long id, EmployeePatchRequest request);

    void deleteEmployee(Long id);

    PageResponse<EmployeeResponse> getAllEmployees(EmployeeSearchRequest request);
}
