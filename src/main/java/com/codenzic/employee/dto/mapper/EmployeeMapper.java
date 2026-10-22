package com.codenzic.employee.dto.mapper;

import com.codenzic.employee.dto.request.EmployeeCreateRequest;
import com.codenzic.employee.dto.request.EmployeePatchRequest;
import com.codenzic.employee.dto.request.EmployeeUpdateRequest;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(EmployeeUpdateRequest request, @MappingTarget Employee employee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void applyPatch(EmployeePatchRequest request, @MappingTarget Employee employee);

    EmployeeResponse toResponse(Employee employee);
}
