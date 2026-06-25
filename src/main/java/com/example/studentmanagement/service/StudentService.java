package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.studentmanagement.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /*public Student saveStudent(Student student ){
        return studentRepository.save(student);
    }*/

    public Student saveStudent(StudentRequestDto dto){
        Student student = new Student();

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());

        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        /*Optional<Student> optionalStudent= studentRepository.findById(id);
        if(optionalStudent.isPresent()) {
            return optionalStudent.get();
        }

        throw new StudentNotFoundException(id);*/
        return  studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student updateStudent(Long id, StudentRequestDto dto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));

        existingStudent.setAge(dto.getAge());
        existingStudent.setName(dto.getName());
        existingStudent.setEmail(dto.getEmail());

        return studentRepository.save(existingStudent);
    }

//    public ResponseEntity<String> deleteStudent(Long id) {
//        if(studentRepository.existsById(id)){
//            studentRepository.deleteById(id);
//            return new ResponseEntity<>("Student deleted successfully", HttpStatus.OK);
//        }
//        return new ResponseEntity<>("Student not Found",HttpStatus.NOT_FOUND);
//    }

    public void deleteStudent(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));
        studentRepository.delete(student);
    }
}
