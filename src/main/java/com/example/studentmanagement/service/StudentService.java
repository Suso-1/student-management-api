package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentNotFoundException;
import org.springframework.stereotype.Service;
import com.example.studentmanagement.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private StudentResponseDto mapToResponseDto(Student student){
        return new StudentResponseDto(student.getId(),student.getName(),student.getEmail(), student.getAge());
    }

    private Student mapToStudent(StudentRequestDto dto){

        Student student = new Student();

        student.setEmail(dto.getEmail());
        student.setName(dto.getName());
        student.setAge(dto.getAge());

        return student;
    }

    public StudentResponseDto saveStudent(StudentRequestDto dto){

        Student student = mapToStudent(dto);

        Student savedStudent= studentRepository.save(student);

        return mapToResponseDto(savedStudent);
    }

    public StudentResponseDto getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(()->
                        new StudentNotFoundException(id));

        return mapToResponseDto(student);
    }

    public List<StudentResponseDto> getAllStudents() {

        List<Student> students = studentRepository.findAll();

        return students.stream()
                .map(this::mapToResponseDto)
                .toList();

    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto dto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));

        existingStudent.setAge(dto.getAge());
        existingStudent.setName(dto.getName());
        existingStudent.setEmail(dto.getEmail());

        Student updatedStudent = studentRepository.save(existingStudent);
        return mapToResponseDto(updatedStudent);
    }

    public void deleteStudent(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));
        studentRepository.delete(student);
    }
}
