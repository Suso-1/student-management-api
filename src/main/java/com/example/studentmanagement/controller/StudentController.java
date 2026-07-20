package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService=studentService;
    }

    @Operation(
            summary = "Create a new student",
            description = "Creates a new student and stores it in the database."
    )
    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(@Valid @RequestBody StudentRequestDto dto) {
        StudentResponseDto responseDto= studentService.saveStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "Get student by Id",
            description = "Retrieves a student using a given Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Student retrieved successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        StudentResponseDto responseDto = studentService.getStudentById(id);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Get all students",
            description = "Retrieves all the students from the database."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Students retrieved successfully."
    )
    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {

        List<StudentResponseDto> responseDtos= studentService.getAllStudents();

        return ResponseEntity.ok(responseDtos);
    }

    @Operation(
            summary = "Update a student",
            description = "Updates an existing student's details."
    )
    @ApiResponses({
            @ApiResponse(
                responseCode = "200",
                description = "Student updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable  Long id  ,
                                            @Valid @RequestBody StudentRequestDto dto) {

        StudentResponseDto responseDto= studentService.updateStudent(id,dto);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Delete a student",
            description = "Deletes a student using given Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Student deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id ) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted Successfully");
    }
}
