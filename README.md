# Employee Management REST API

A RESTful Employee Management application built with Java, Spring Boot, Spring Data JPA, MySQL, and Gradle. The project demonstrates layered backend architecture, CRUD operations, request validation, centralized exception handling, pagination, search, status filtering, partial updates, and OpenAPI documentation.

## Features

- Create an employee.
- Retrieve an employee by ID.
- Retrieve employees using pagination.
- Search employees by first name, last name, or email.
- Filter employees by status (`ACTIVE` or `INACTIVE`).
- Sort paginated results by supported fields and direction.
- Replace employee details using `PUT`.
- Partially update employee details using `PATCH`.
- Delete an employee.
- Validate incoming request data with Jakarta Bean Validation.
- Return consistent JSON error responses through a global exception handler.
- Enforce unique employee email addresses.
- Generate interactive API documentation with Swagger UI.
- Run automated unit, MVC, validation, mapper, specification, and integration tests.

## Technology stack

- Java 21
- Spring Boot (version configured in `build.gradle`)
- Gradle Wrapper
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL
- Jakarta Bean Validation
- MapStruct
- Lombok
- Springdoc OpenAPI / Swagger UI
- JUnit 5, Mockito, Spring Boot Test, and H2 for tests

## Architecture

The code is organized into layers:

```text
src/main/java/com/codenzic/employee/
├── EmployeeApplication.java
├── config/
│   └── OpenApiConfig.java
├── controller/
│   └── EmployeeController.java
├── dto/
│   ├── mapper/
│   ├── request/
│   └── response/
├── entity/
│   └── enums/
├── exception/
├── repository/
│   └── specification/
├── service/
│   └── impl/
└── utils/
```

### Layer responsibilities

- **Controller:** Maps HTTP requests to service operations and returns HTTP responses.
- **Service:** Implements employee business rules, including duplicate-email checks and not-found handling.
- **Repository:** Uses Spring Data JPA for persistence and query execution.
- **Entity:** Maps the `Employee` domain model to the `employees` database table.
- **Request/response DTOs:** Define the API contract independently of the persistence entity.
- **Mapper:** Uses MapStruct to convert between entities and DTOs.
- **Specification:** Builds optional search and status-filter criteria.
- **Exception handler:** Converts expected and unexpected errors into structured JSON responses.
- **OpenAPI configuration:** Defines API metadata for Swagger UI.

## Prerequisites

Install the following:

1. **JDK 21**
2. **MySQL Server** (a locally installed MySQL instance is suitable)
3. An IDE such as IntelliJ IDEA (optional)
4. Git (for cloning and submission)

The repository includes the Gradle Wrapper, so a separate Gradle installation is not required.

Check your Java installation:

```bash
java -version
```

## Database setup

Create the database in MySQL:

```sql
CREATE DATABASE employee;
```

The application is configured to connect to a MySQL database named `employee` on `localhost:3306`.

### Configure credentials securely

**Before committing or publishing this project, make sure `src/main/resources/application.yaml` does not contain a real database password.** The uploaded project configuration contained a hard-coded credential. Replace it, and rotate that database password if it is real or has been reused.

Use environment variables instead. Update `src/main/resources/application.yaml` so its datasource configuration uses environment-variable placeholders, for example:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/employee}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true

server:
  port: ${SERVER_PORT:8080}
```

Keep only one active configuration source for each setting. The repository also contains `application.properties` with the application name; Spring Boot can load both properties and YAML files, but avoid defining conflicting values in both.

Set the environment variables before starting the application.

**Windows PowerShell:**

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_local_mysql_password"
$env:DB_URL = "jdbc:mysql://localhost:3306/employee"
```

**Linux/macOS:**

```bash
export DB_USERNAME=root
export DB_PASSWORD='your_local_mysql_password'
export DB_URL='jdbc:mysql://localhost:3306/employee'
```

Use your own local MySQL username and password. Do not put real credentials in source control, screenshots, or the README.

The current development configuration uses `spring.jpa.hibernate.ddl-auto: update`, which lets Hibernate update the schema based on the entity mapping. For production systems, use a database migration tool such as Flyway or Liquibase and review schema changes explicitly.

## Run the application

Clone the repository, then open a terminal in the project root (the directory containing `build.gradle` and `gradlew`).

**Windows:**

```powershell
.\gradlew.bat bootRun
```

**Linux/macOS:**

```bash
./gradlew bootRun
```

The application listens on port `8080` by default.

To build the project:

```bash
./gradlew clean build
```

On Windows, use `.\gradlew.bat clean build`.

## API reference

Base URL:

```text
http://localhost:8080/api/v1/employees
```

| Method | Endpoint | Description | Typical success response |
|---|---|---|---|
| `POST` | `/api/v1/employees` | Create an employee | `201 Created` |
| `GET` | `/api/v1/employees` | List employees with pagination and filters | `200 OK` |
| `GET` | `/api/v1/employees/{id}` | Retrieve one employee | `200 OK` |
| `PUT` | `/api/v1/employees/{id}` | Replace editable employee details | `200 OK` |
| `PATCH` | `/api/v1/employees/{id}` | Update only supplied fields | `200 OK` |
| `DELETE` | `/api/v1/employees/{id}` | Delete an employee | `200 OK` with a confirmation message |

A missing employee is reported as `404 Not Found`. Invalid request data or query parameters are reported as `400 Bad Request`. Duplicate email or database integrity conflicts are reported as `409 Conflict`.

### Create an employee

`POST /api/v1/employees`

Example JSON body:

```json
{
  "firstName": "exampleFirst",
  "lastName": "exampleFirst",
  "email": "example@example.com",
  "department": "Engineering",
  "salary": 45000.00,
  "joiningDate": "2025-01-15"
}
```

`firstName`, `lastName`, `email`, `salary`, and `joiningDate` are required. The department is optional. Names must not be blank, the email must have a valid format and be unique, salary must not be negative, and joining date cannot be in the future.

The `status` field is managed by the application entity default when creating a new employee; the current create request does not accept it.

### List, search, filter, and sort employees

`GET /api/v1/employees`

Supported query parameters:

| Parameter | Default | Description                                                                                           |
|---|---:|-------------------------------------------------------------------------------------------------------|
| `page` | `0` | Zero-based page number; must be zero or greater                                                       |
| `size` | `10` | Page size from 1 to 100                                                                               |
| `search` | — | Optional text search across first name, last name, and email                                          |
| `status` | — | Optional employee status: `ACTIVE` or `INACTIVE`                                                      |
| `sortBy` | `id` | Sort field: `id`, `firstName`, `lastName`, `email`, `department`, `salary`, `joiningDate` or `status` |
| `direction` | `asc` | Sort direction: `asc` or `desc`                                                                       |

Examples:

```text
GET /api/v1/employees?page=0&size=10
GET /api/v1/employees?search=arun
GET /api/v1/employees?status=ACTIVE
GET /api/v1/employees?search=arun&status=ACTIVE
GET /api/v1/employees?page=0&size=5&sortBy=lastName&direction=desc
```

The response includes `content`, `page`, `size`, `totalElements`, `totalPages`, `first`, and `last`.

### Retrieve an employee

```text
GET /api/v1/employees/1
```

Replace `1` with an employee ID that exists in the database.

### Update an employee with PUT

`PUT /api/v1/employees/{id}` replaces the editable fields, so provide all required fields.

```json
{
  "firstName": "exampleFirst",
  "lastName": "exampleLast",
  "email": "example@example.com",
  "department": "Platform Engineering",
  "salary": 55000.00,
  "joiningDate": "2025-01-15",
  "status": "ACTIVE"
}
```

### Partially update an employee with PATCH

`PATCH /api/v1/employees/{id}` updates only the fields provided in the request. At least one field must be supplied.

```json
{
  "department": "Platform Engineering",
  "salary": 60000.00
}
```

### Delete an employee

```text
DELETE /api/v1/employees/1
```

The current controller returns `200 OK` with a JSON confirmation message after successful deletion.

## Error response format

Errors use a common response structure. For example, a request for a missing employee may return:

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with ID: 99",
  "path": "/api/v1/employees/99",
  "fieldErrors": {}
}
```

The timestamp and message values depend on the actual request. Validation failures include field-specific messages in `fieldErrors`.

## Swagger / OpenAPI documentation

Start the application and open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger UI lets you inspect the documented endpoints and execute requests directly from the browser. Creating, updating, or deleting data through Swagger changes the configured database.

## Testing

Tests are located under `src/test/java`. The project includes tests for controller behavior, service logic, DTO validation and mapping, pagination utilities, search specifications, exception handling, and API integration.

Run all tests:

**Windows:**

```powershell
.\gradlew.bat clean test
```

**Linux/macOS:**

```bash
./gradlew clean test
```

The project includes H2 as a test runtime dependency and a test configuration under `src/test/resources/application-test.properties`. Integration tests that use this profile should activate it explicitly, for example with `@ActiveProfiles("test")`, or by supplying the relevant Spring profile when running tests.

Gradle test reports are generated at:

```text
build/reports/tests/test/index.html
```

## Project configuration notes

- The default application port is `8080`.
- The MySQL database name is `employee`.
- Hibernate schema management is configured for development with `ddl-auto: update`.
- Do not commit database credentials or environment-specific secrets.
- No Postman collection is included; Swagger UI is provided for interactive API exploration.

## Git ignore and repository hygiene

The repository should include source code, Gradle build files, the Gradle Wrapper scripts and wrapper configuration, application configuration without secrets, tests, and this README.

Generated output and local-only files such as `.gradle/`, `build/`, IDE metadata, logs, and machine-specific configuration should not be committed.

## Author

**Employee Management REST API — Hariprasad R**
