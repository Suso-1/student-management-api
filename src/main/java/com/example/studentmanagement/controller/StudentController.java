package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
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

    /*@PostMapping
    public Student createStudent(@RequestBody Student student){ return studentService.saveStudent(student); }*/

    /*@PostMapping
    public Student createStudent(@Valid @RequestBody StudentRequestDto dto) {return studentService.saveStudent(dto);}*/

    @PostMapping
    public StudentResponseDto createStudent(@Valid @RequestBody StudentRequestDto dto) {return studentService.saveStudent(dto);}

    @GetMapping("/{id}")
    public StudentResponseDto getStudentById(@PathVariable Long id) { return studentService.getStudentById(id);}

    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PutMapping("/{id}")
    public StudentResponseDto updateStudent(@PathVariable  Long id  ,
                                            @Valid @RequestBody StudentRequestDto dto) {
        return studentService.updateStudent(id,dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id ) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted Successfully");
    }
}
