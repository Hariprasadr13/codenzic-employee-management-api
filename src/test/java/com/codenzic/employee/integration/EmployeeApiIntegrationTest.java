package com.codenzic.employee.integration;

import com.codenzic.employee.entity.Employee;
import com.codenzic.employee.entity.enums.EmployeeStatus;
import com.codenzic.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeApiIntegrationTest {

    private static final String BASE_URL = "/api/v1/employees";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void cleanDatabase() {
        employeeRepository.deleteAll();
    }

    // CREATE

    @Test
    void createEmployee_returns201AndLocationHeader() throws Exception {
        String request = createEmployeeRequest(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", "95000.00", "2024-01-15"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", containsString(BASE_URL + "/")
                ))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));

        assertEquals(1, employeeRepository.count());
        assertEquals(
                EmployeeStatus.ACTIVE,
                employeeRepository.findAll().getFirst().getStatus()
        );
    }

    @Test
    void createEmployee_withInvalidFields_returns400() throws Exception {
        String request = """
                {
                  "firstName": "",
                  "lastName": "Lovelace",
                  "email": "not-an-email",
                  "salary": -1,
                  "joiningDate": "2099-01-01"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void createEmployee_withDuplicateEmail_returns409() throws Exception {
        saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        String request = createEmployeeRequest(
                "Grace", "Hopper", "ada@example.com",
                "Engineering", "85000.00", "2023-03-10"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        assertEquals(1, employeeRepository.count());
    }

    // GET BY ID

    @Test
    void getEmployeeById_whenFound_returns200() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(get(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void getEmployeeById_whenMissing_returns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // GET ALL: PAGINATION, SEARCH AND STATUS FILTER

    @Test
    void getEmployees_supportsPaginationSearchAndStatusFilter()
            throws Exception {
        saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );
        saveEmployee(
                "Ada", "Byron", "ada.byron@example.com",
                "Research", EmployeeStatus.INACTIVE
        );
        saveEmployee(
                "Grace", "Hopper", "grace@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "ADA")
                        .param("status", "ACTIVE")
                        .param("sortBy", "firstName")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value("Ada"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getEmployees_withInvalidPage_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL).param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmployees_withInvalidStatus_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL).param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmployees_withInvalidSortBy_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/v1/employees")
                        .param("sortBy", "unknownField"))
                .andExpect(status().isBadRequest());
    }

    // PUT: FULL UPDATE

    @Test
    void updateEmployee_replacesEditableFields() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        String request = updateEmployeeRequest(
                "Ada", "King", "ada.king@example.com",
                "Research", "105000.00", "2024-02-01",
                EmployeeStatus.INACTIVE
        );

        mockMvc.perform(put(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.lastName").value("King"))
                .andExpect(jsonPath("$.email").value("ada.king@example.com"))
                .andExpect(jsonPath("$.department").value("Research"));

        Employee updated = employeeRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(EmployeeStatus.INACTIVE, updated.getStatus());
        assertEquals("King", updated.getLastName());
        assertEquals("Research", updated.getDepartment());
    }

    @Test
    void updateEmployee_whenMissing_returns404() throws Exception {
        String request = updateEmployeeRequest(
                "Ada", "King", "ada.king@example.com",
                "Research", "105000.00", "2024-02-01",
                EmployeeStatus.ACTIVE
        );

        mockMvc.perform(put(BASE_URL + "/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployee_withDuplicateEmail_returns409() throws Exception {
        saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        Employee second = saveEmployee(
                "Grace", "Hopper", "grace@example.com",
                "Research", EmployeeStatus.ACTIVE
        );

        String request = updateEmployeeRequest(
                "Grace", "Hopper", "ada@example.com",
                "Research", "105000.00", "2024-02-01",
                EmployeeStatus.ACTIVE
        );

        mockMvc.perform(put(BASE_URL + "/{id}", second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict());

        assertEquals(2, employeeRepository.count());
    }

    @Test
    void updateEmployee_withoutStatus_returns400() throws Exception {
        String request = """
                {
                  "firstName": "Ada",
                  "lastName": "King",
                  "email": "ada.king@example.com",
                  "department": "Research",
                  "salary": 105000.00,
                  "joiningDate": "2024-02-01"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status")
                        .value("Employee status is required"));
    }

    // PATCH: PARTIAL UPDATE

    @Test
    void patchEmployee_updatesOnlyProvidedFields() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"department":"Research"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.department").value("Research"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));

        Employee updated = employeeRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(EmployeeStatus.ACTIVE, updated.getStatus());
    }

    @Test
    void patchEmployee_updatesStatusWhenProvided() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"INACTIVE"}
                                """))
                .andExpect(status().isOk());

        Employee updated = employeeRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(EmployeeStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void patchEmployee_withoutStatus_preservesExistingStatus()
            throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"department":"Research"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Research"));

        Employee updated = employeeRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(EmployeeStatus.ACTIVE, updated.getStatus());
        assertEquals("Research", updated.getDepartment());
    }

    @Test
    void patchEmployee_whenMissing_returns404() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"department":"Research"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchEmployee_withInvalidEmail_returns400() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"invalid-email"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchEmployee_withEmptyRequest_shouldReturnBadRequest() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // DELETE

    @Test
    void deleteEmployee_deletesExistingEmployee() throws Exception {
        Employee saved = saveEmployee(
                "Ada", "Lovelace", "ada@example.com",
                "Engineering", EmployeeStatus.ACTIVE
        );

        mockMvc.perform(delete(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Employee deleted successfully"));

        assertFalse(employeeRepository.existsById(saved.getId()));
    }

    @Test
    void deleteEmployee_whenMissing_returns404() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    // TEST DATA HELPERS

    private Employee saveEmployee(
            String firstName,
            String lastName,
            String email,
            String department,
            EmployeeStatus status
    ) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setDepartment(department);
        employee.setSalary(new BigDecimal("75000.00"));
        employee.setJoiningDate(LocalDate.of(2023, 1, 10));
        employee.setStatus(status);

        return employeeRepository.saveAndFlush(employee);
    }

    private String createEmployeeRequest(
            String firstName,
            String lastName,
            String email,
            String department,
            String salary,
            String joiningDate
    ) {
        return objectMapper.writeValueAsString(new CreateRequest(
                firstName,
                lastName,
                email,
                department,
                new BigDecimal(salary),
                LocalDate.parse(joiningDate)
        ));
    }

    private String updateEmployeeRequest(
            String firstName,
            String lastName,
            String email,
            String department,
            String salary,
            String joiningDate,
            EmployeeStatus status
    ) {
        return objectMapper.writeValueAsString(new UpdateRequest(
                firstName,
                lastName,
                email,
                department,
                new BigDecimal(salary),
                LocalDate.parse(joiningDate),
                status
        ));
    }

    private record CreateRequest(
            String firstName,
            String lastName,
            String email,
            String department,
            BigDecimal salary,
            LocalDate joiningDate
    ) {
    }

    private record UpdateRequest(
            String firstName,
            String lastName,
            String email,
            String department,
            BigDecimal salary,
            LocalDate joiningDate,
            EmployeeStatus status
    ) {
    }
}