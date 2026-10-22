package com.codenzic.employee.exception;

public class DuplicateEmployeeEmailException extends RuntimeException {

    public DuplicateEmployeeEmailException(String email) {
        super("An employee with email " + email + "already exists");
    }
}
