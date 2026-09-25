package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Department;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentNotFoundException;
import com.example.studentmanagement.repository.DepartmentRepository;
import com.example.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private StudentService studentService;


    @Test
    void shouldSaveStudentSuccessfully() {

        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto(
                "Rahul", "rahul@gmail.com", 21,"9876543210",1L
        );
        Department department= new Department(1L,"Computer Science");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Student savedStudent = new Student(
                1L, "Rahul", "rahul@gmail.com", 21, "9876543210"
        );
        savedStudent.setDepartment(department);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // Act
        StudentResponseDto response = studentService.saveStudent(requestDto);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Rahul", response.getName());
        assertEquals("rahul@gmail.com", response.getEmail());
        assertEquals(21, response.getAge());
        assertEquals("9876543210",response.getPhoneNumber());
        assertEquals(1L,response.getDepartmentId());
        assertEquals("Computer Science",response.getDepartmentName());

        // Verify
        //verify(studentRepository).save(any(Student.class));
        verify(departmentRepository).findById(1L);
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
                "Rahul", "rahul@gmail.com", 21, "9876543210",1L
        );
        Department department= new Department(1L,"Computer Science");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Student savedStudent= new Student(1L, "Rahul", "rahul@gmail.com", 21, "9876543210");
        savedStudent.setDepartment(department);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

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
        assertEquals("9876543210",capturedStudent.getPhoneNumber());
        assertEquals(department,capturedStudent.getDepartment());
    }

    @Test
    void shouldUpdateStudentSuccessfully() {

        // Arrange
        Long id = 1L;
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21, "9876543210",1L);

        Student existingStudent = new Student(1L, "Old Name", "old@gmail.com", 20, "9123456780");
        Department department= new Department(1L,"Computer Science");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

//        when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

//        ArgumentCaptor<Student> captor= ArgumentCaptor.forClass(Student.class);

        // Act
        StudentResponseDto responseDto = studentService.updateStudent(id, requestDto);

        // Verify
        verify(studentRepository).findById(id);
        verify(departmentRepository).findById(1L);
        verify(studentRepository,never()).save(any(Student.class));

        // Get captured object
//        Student capturedStudent = captor.getValue();

        // Assert Student was updated

        assertEquals("Rahul", existingStudent.getName());
        assertEquals("rahul@gmail.com", existingStudent.getEmail());
        assertEquals(21, existingStudent.getAge());
        assertEquals("9876543210", existingStudent.getPhoneNumber());
        assertEquals(department, existingStudent.getDepartment());

        // Assert Response

        assertEquals("Rahul", responseDto.getName());
        assertEquals("rahul@gmail.com", responseDto.getEmail());
        assertEquals(21, responseDto.getAge());
        assertEquals("9876543210", responseDto.getPhoneNumber());
        assertEquals(1L, responseDto.getDepartmentId());
        assertEquals("Computer Science", responseDto.getDepartmentName());
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenUpdatingNonExistingStudent(){

        // Arrange
//        Long id = 99L;
        StudentRequestDto requestDto= new StudentRequestDto("Rahul", "rahul@gmail.com", 21,"9876543210",1L);
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
                1L,"Rahul","rahul@gmail.com",21, "9876543210"
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

    /*@Test
    void shouldReturnAllStudentsSuccessfully() {

        // Arrange
        Student student1= new Student(1L,"Rahul","rahul@gmail.com",21,"9876543210");
        Student student2= new Student(2L, "Amit", "amit@gmail.com",23, "9123456780");

        Department cs = new Department(1L, "Computer Science");
        Department it = new Department(2L, "Information Tech");

        student1.setDepartment(cs);
        student2.setDepartment(it);

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
        assertEquals("9876543210", responseDtos.get(0).getPhoneNumber());
        assertEquals(1L, responseDtos.get(0).getDepartmentId());
        assertEquals("Computer Science", responseDtos.get(0).getDepartmentName());

        assertEquals(2L, responseDtos.get(1).getId());
        assertEquals("Amit",responseDtos.get(1).getName());
        assertEquals("amit@gmail.com", responseDtos.get(1).getEmail());
        assertEquals(23,responseDtos.get(1).getAge());
        assertEquals("9123456780", responseDtos.get(1).getPhoneNumber());
        assertEquals(2L, responseDtos.get(1).getDepartmentId());
        assertEquals("Information Tech", responseDtos.get(1).getDepartmentName());

        // Verify
        verify(studentRepository).findAll();
    }*/

    /*@Test
    void shouldReturnEmptyListWhenNoStudentsExist() {

        // Arrange
        when(studentRepository.findAll()).thenReturn(List.of());

        // Act
        List<StudentResponseDto> responseDtos= studentService.getAllStudents();

        // Assert
        assertTrue(responseDtos.isEmpty());

        // Verify
        verify(studentRepository).findAll();
    }*/

    @Test
    void shouldReturnStudentWhenIdExists(){

        // Arrange
        Long id= 1L;
        Student student= new Student(1L,"Rahul","rahul@gmail.com",21, "9876543210");
        Department department=new Department(1L,"Computer Science");
        student.setDepartment(department);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        // Act
        StudentResponseDto response = studentService.getStudentById(id);

        // Assert
        assertEquals(1L,response.getId());
        assertEquals("Rahul",response.getName());
        assertEquals("rahul@gmail.com",response.getEmail());
        assertEquals(21,response.getAge());
        assertEquals("9876543210", response.getPhoneNumber());
        assertEquals(1L, response.getDepartmentId());
        assertEquals("Computer Science", response.getDepartmentName());

        // Verify
        verify(studentRepository).findById(id);
    }

    @Test
    void shouldReturnStudentWhenEmailExists() {
        String email = "rahul@gmail.com";

        Department department =
                new Department(1L, "Computer Science");

        Student student =
                new Student(
                        1L,
                        "Rahul",
                        email,
                        21,
                        "9876543210"
                );

        student.setDepartment(department);

        when(studentRepository.findByEmail(email))
                .thenReturn(Optional.of(student));

        StudentResponseDto response =
                studentService.getStudentByEmail(email);

        verify(studentRepository).findByEmail(email);

        assertEquals(1L, response.getId());
        assertEquals("Rahul", response.getName());
        assertEquals(email, response.getEmail());
        assertEquals(1L, response.getDepartmentId());
        assertEquals("Computer Science", response.getDepartmentName());
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenEmailDoesNotExist() {

        String email = "unknown@gmail.com";

        when(studentRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> studentService.getStudentByEmail(email)
        );

        verify(studentRepository).findByEmail(email);
    }

    @Test
    void shouldReturnPaginatedStudentsSuccessfully() {

        Pageable pageable = PageRequest.of(0, 2);

        Department department =
                new Department(1L, "Computer Science");

        Student student1 =
                new Student(1L, "Rahul", "rahul@gmail.com", 21, "9876543210");

        Student student2 =
                new Student(2L,"Amit", "amit@gmail.com", 22, "9876543211");

        student1.setDepartment(department);
        student2.setDepartment(department);

        Page<Student> studentPage = new PageImpl<>(
                        List.of(student1, student2), pageable,
                        2);

        when(studentRepository.findAll(pageable))
                .thenReturn(studentPage);

        Page<StudentResponseDto> result =
                studentService.getAllStudents(null,pageable);

        verify(studentRepository).findAll(pageable);
        verify(studentRepository, never()).findByDepartmentId(anyLong(), any(Pageable.class));

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("Rahul", result.getContent().get(0).getName());
    }

    @Test
    void shouldReturnStudentsByDepartmentSuccessfully() {

        // Arrange
        Long departmentId = 1L;

        Pageable pageable = PageRequest.of(0, 2);

        Department department =
                new Department(1L, "Computer Science");

        Student student1 =
                new Student(1L, "Rahul", "rahul@gmail.com", 21, "9876543210");

        Student student2 =
                new Student(2L, "Amit", "amit@gmail.com", 22, "9876543211");

        student1.setDepartment(department);
        student2.setDepartment(department);

        Page<Student> studentPage = new PageImpl<>(List.of(student1, student2), pageable, 2);

        when(studentRepository.findByDepartmentId(departmentId, pageable)).thenReturn(studentPage);

        // Act
        Page<StudentResponseDto> result = studentService.getAllStudents(departmentId, pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals("Rahul", result.getContent().get(0).getName());
        assertEquals("Amit", result.getContent().get(1).getName());

        // Verify
        verify(studentRepository).findByDepartmentId(departmentId, pageable);

        verify(studentRepository, never()).findAll(any(Pageable.class));
    }
    @Test
    void shouldReturnEmptyPageWhenNoStudentsExist() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Student> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(studentRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<StudentResponseDto> result = studentService.getAllStudents(null,pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(studentRepository).findAll(pageable);
    }

    @Test
    void shouldReturnStudentsSortedByNameAscending() {

        Pageable pageable =
                PageRequest.of(0, 2, Sort.by("name").ascending());

        Department department = new Department(1L, "Computer Science");

        Student student1 =
                new Student(1L, "Amit", "amit@gmail.com", 22, "9876543211");

        Student student2 =
                new Student(2L, "Rahul", "rahul@gmail.com", 21, "9876543210");

        student1.setDepartment(department);
        student2.setDepartment(department);

        Page<Student> studentPage =
                new PageImpl<>(List.of(student1, student2), pageable, 2);

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<StudentResponseDto> result = studentService.getAllStudents(null,pageable);

        verify(studentRepository).findAll(pageable);

        assertEquals("Amit", result.getContent().get(0).getName());
        assertEquals("Rahul", result.getContent().get(1).getName());

        assertEquals(Sort.by("name").ascending(), pageable.getSort());
    }

}






























