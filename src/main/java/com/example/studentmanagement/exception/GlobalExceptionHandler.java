package com.example.studentmanagement.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFoundException(StudentNotFoundException ex) {
       return new ResponseEntity<>(
               ex.getMessage(),
               HttpStatus.NOT_FOUND
       );
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidExceptions(
            MethodArgumentNotValidException ex ) {

       Map<String, String > errors = new HashMap<>();

       ex.getBindingResult()
               .getFieldErrors()
               .forEach(error->
                       errors.put(error.getField(), error.getDefaultMessage())
               );

       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(errors);
   }

   @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex){
       return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists ");
   }

}
