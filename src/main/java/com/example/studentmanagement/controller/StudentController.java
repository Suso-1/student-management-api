package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.service.StudentService;
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

    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(@Valid @RequestBody StudentRequestDto dto) {
        StudentResponseDto responseDto= studentService.saveStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        StudentResponseDto responseDto = studentService.getStudentById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {

        List<StudentResponseDto> responseDtos= studentService.getAllStudents();

        return ResponseEntity.ok(responseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable  Long id  ,
                                            @Valid @RequestBody StudentRequestDto dto) {

        StudentResponseDto responseDto= studentService.updateStudent(id,dto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id ) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted Successfully");
    }
}
