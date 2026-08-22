package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentNotFoundException;
import com.example.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;


    @Test
    void shouldSaveStudentSuccessfully() {

        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto(
                "Rahul", "rahul@gmail.com", 21
        );

        Student savedStudent = new Student(
                1L, "Rahul", "rahul@gmail.com", 21
        );

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // Act
        StudentResponseDto response = studentService.saveStudent(requestDto);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Rahul", response.getName());
        assertEquals("rahul@gmail.com", response.getEmail());
        assertEquals(21, response.getAge());

        // Verify
        //verify(studentRepository).save(any(Student.class));
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenStudentDoesNotExist() {

        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                StudentNotFoundException.class, () -> studentService.getStudentById(99L)
        );

        // Verify
        verify(studentRepository).findById(99L);
    }

    //argumentcaptor
    @Test
    void shouldMapRequestDtoToStudentCorrectly() {

        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto(
                "Rahul", "rahul@gmail.com", 21
        );

        when(studentRepository.save(any(Student.class))).thenReturn(new Student());

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);

        // Act
        studentService.saveStudent(requestDto);

        // Verify
        verify(studentRepository).save(captor.capture());

        // Get the actual student sent to the repository
        Student capturedStudent = captor.getValue();

        // Assert
        assertEquals("Rahul", capturedStudent.getName());
        assertEquals("rahul@gmail.com", capturedStudent.getEmail());
        assertEquals(21, capturedStudent.getAge());
    }

    @Test
    void shouldUpdateStudentSuccessfully() {

        // Arrange
        Long id = 1L;
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21);

        Student existingStudent = new Student(1L, "Old Name", "old@gmail.com", 20);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));

        when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

        ArgumentCaptor<Student> captor= ArgumentCaptor.forClass(Student.class);

        // Act
        StudentResponseDto responseDto = studentService.updateStudent(id, requestDto);

        // Verify
        verify(studentRepository).findById(id);
        verify(studentRepository).save(captor.capture());

        // Get captured object
        Student capturedStudent = captor.getValue();

        // Assert
        assertEquals("Rahul", capturedStudent.getName());
        assertEquals("rahul@gmail.com", capturedStudent.getEmail());
        assertEquals(21, capturedStudent.getAge());

        assertEquals("Rahul", responseDto.getName());
        assertEquals("rahul@gmail.com", responseDto.getEmail());
        assertEquals(21, responseDto.getAge());
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenUpdatingNonExistingStudent(){

        // Arrange
        Long id = 99L;
        StudentRequestDto requestDto= new StudentRequestDto("Rahul", "rahul@gmail.com", 21);
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                StudentNotFoundException.class, ()-> studentService.updateStudent(99L,requestDto)
        );

        // Verify
        verify(studentRepository,never())
                .save(any(Student.class));
    }

    @Test
    void shouldDeleteStudentSuccessfully(){
        //Arrange
        Student existingStudent= new Student(
                1L,"Rahul","rahul@gmail.com",21
        );

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));

        // Act
        studentService.deleteStudent(1L);

        // Verify
        verify(studentRepository).findById(1L);
        verify(studentRepository,times(1)).delete(existingStudent);
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenDeletingNonExistingStudent(){
        // Arrange
        Long id = 99L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                StudentNotFoundException.class, ()-> studentService.deleteStudent(id)
        );

        // Verify
        verify(studentRepository,never()).delete(any(Student.class));
    }

    @Test
    void shouldReturnAllStudentsSuccessfully() {

        // Arrange
        Student student1= new Student(1L,"Rahul","rahul@gmail.com",21);
        Student student2= new Student(2L, "Amit", "amit@gmail.com",23);

        List<Student> students= List.of(student1, student2);
        when(studentRepository.findAll()).thenReturn(students);

        // Act
        List<StudentResponseDto> responseDtos= studentService.getAllStudents();

        // Assert
        assertEquals(2, responseDtos.size());

        assertEquals(1L, responseDtos.get(0).getId());
        assertEquals("Rahul",responseDtos.get(0).getName());
        assertEquals("rahul@gmail.com", responseDtos.get(0).getEmail());
        assertEquals(21,responseDtos.get(0).getAge());

        assertEquals(2L, responseDtos.get(1).getId());
        assertEquals("Amit",responseDtos.get(1).getName());
        assertEquals("amit@gmail.com", responseDtos.get(1).getEmail());
        assertEquals(23,responseDtos.get(1).getAge());

        // Verify
        verify(studentRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoStudentsExist() {

        // Arrange
        when(studentRepository.findAll()).thenReturn(List.of());

        // Act
        List<StudentResponseDto> responseDtos= studentService.getAllStudents();

        // Assert
        assertTrue(responseDtos.isEmpty());

        // Verify
        verify(studentRepository).findAll();
    }

    @Test
    void shouldReturnStudentWhenIdExists(){

        // Arrange
        Long id= 1L;
        Student student= new Student(1L,"Rahul","rahul@gmail.com",21);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        // Act
        StudentResponseDto response = studentService.getStudentById(id);

        // Assert
        assertEquals(1L,response.getId());
        assertEquals("Rahul",response.getName());
        assertEquals("rahul@gmail.com",response.getEmail());
        assertEquals(21,response.getAge());

        // Verify
        verify(studentRepository).findById(id);
    }

}






























