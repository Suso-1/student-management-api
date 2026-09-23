package com.example.studentmanagement.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(Long id) {
        super("Department not Found with id :"+id);
    }
}
