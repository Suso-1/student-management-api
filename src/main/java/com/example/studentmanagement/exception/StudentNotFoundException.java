package com.example.studentmanagement.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) {
        super("Student Not Found with id :"+id);
    }
}
