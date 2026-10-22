package com.codenzic.employee.dto.mapper;

import com.codenzic.employee.dto.request.EmployeeCreateRequest;
import com.codenzic.employee.dto.request.EmployeePatchRequest;
import com.codenzic.employee.dto.request.EmployeeUpdateRequest;
import com.codenzic.employee.dto.response.EmployeeResponse;
import com.codenzic.employee.entity.Employee;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-22T13:06:43+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12.1 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toEntity(EmployeeCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Employee employee = new Employee();

        employee.setFirstName( request.firstName() );
        employee.setLastName( request.lastName() );
        employee.setEmail( request.email() );
        employee.setDepartment( request.department() );
        employee.setSalary( request.salary() );
        employee.setJoiningDate( request.joiningDate() );

        return employee;
    }

    @Override
    public void updateEntity(EmployeeUpdateRequest request, Employee employee) {
        if ( request == null ) {
            return;
        }

        employee.setFirstName( request.firstName() );
        employee.setLastName( request.lastName() );
        employee.setEmail( request.email() );
        employee.setDepartment( request.department() );
        employee.setSalary( request.salary() );
        employee.setJoiningDate( request.joiningDate() );
        employee.setStatus( request.status() );
    }

    @Override
    public void applyPatch(EmployeePatchRequest request, Employee employee) {
        if ( request == null ) {
            return;
        }

        if ( request.firstName() != null ) {
            employee.setFirstName( request.firstName() );
        }
        if ( request.lastName() != null ) {
            employee.setLastName( request.lastName() );
        }
        if ( request.email() != null ) {
            employee.setEmail( request.email() );
        }
        if ( request.department() != null ) {
            employee.setDepartment( request.department() );
        }
        if ( request.salary() != null ) {
            employee.setSalary( request.salary() );
        }
        if ( request.joiningDate() != null ) {
            employee.setJoiningDate( request.joiningDate() );
        }
        if ( request.status() != null ) {
            employee.setStatus( request.status() );
        }
    }

    @Override
    public EmployeeResponse toResponse(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String department = null;
        BigDecimal salary = null;
        LocalDate joiningDate = null;

        id = employee.getId();
        firstName = employee.getFirstName();
        lastName = employee.getLastName();
        email = employee.getEmail();
        department = employee.getDepartment();
        salary = employee.getSalary();
        joiningDate = employee.getJoiningDate();

        EmployeeResponse employeeResponse = new EmployeeResponse( id, firstName, lastName, email, department, salary, joiningDate );

        return employeeResponse;
    }
}
