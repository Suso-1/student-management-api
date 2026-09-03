package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentNotFoundException;
import org.springframework.stereotype.Service;
import com.example.studentmanagement.repository.StudentRepository;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private StudentResponseDto mapToResponseDto(Student student){
        return new StudentResponseDto(student.getId(),student.getName(),student.getEmail(), student.getAge(), student.getPhoneNumber());
    }

    private Student mapToStudent(StudentRequestDto dto){

        Student student = new Student();

        student.setEmail(dto.getEmail());
        student.setName(dto.getName());
        student.setAge(dto.getAge());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setRegisteredAt(Instant.now());

        return student;
    }

    public StudentResponseDto saveStudent(StudentRequestDto dto){

        logger.info("Creating student with email: {} ", dto.getEmail());

        Student student = mapToStudent(dto);

        Student savedStudent= studentRepository.save(student);

        logger.info("Student created with ID: {}",savedStudent.getId());

        return mapToResponseDto(savedStudent);
    }

    public StudentResponseDto getStudentById(Long id) {

        logger.info("Fetching student with ID: {}",id);

        Student student = studentRepository.findById(id)
                .orElseThrow(()->
                        new StudentNotFoundException(id));

        return mapToResponseDto(student);
    }

    public List<StudentResponseDto> getAllStudents() {

        logger.info("Fetching all students.");

        List<Student> students = studentRepository.findAll();

        logger.info("Retrieved {} students ",students.size());

        return students.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto dto) {

        logger.info("Updating student with ID : {}",id);

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));

        existingStudent.setAge(dto.getAge());
        existingStudent.setName(dto.getName());
        existingStudent.setEmail(dto.getEmail());
        existingStudent.setPhoneNumber(dto.getPhoneNumber());

        Student updatedStudent = studentRepository.save(existingStudent);

        logger.info("Student updated successfully with Student id: {}",updatedStudent.getId());

        return mapToResponseDto(updatedStudent);
    }

    public void deleteStudent(Long id){

        logger.info("Deleting student with ID: {}",id);

        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));

        studentRepository.delete(student);

        logger.info("Student deleted successfully with ID: {}",id);
    }
}
