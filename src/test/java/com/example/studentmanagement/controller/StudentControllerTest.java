package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentNotFoundException;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @Test
    void shouldCreateStudentAndReturn201() throws Exception {

        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21);
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", "rahul@gmail.com", 21);
        String requestJson= objectMapper.writeValueAsString(requestDto);
        when(studentService.saveStudent(any(StudentRequestDto.class))).thenReturn(responseDto);

        // Act + Assert
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.age").value(21));

        // Verify
        verify(studentService).saveStudent(any(StudentRequestDto.class));
    }

    @Test
    void shouldRejectInvalidStudentRequest() throws Exception {

        // Arrange
        StudentRequestDto requestDto= new StudentRequestDto("","wrongEmail",17);
        String requestJson= objectMapper.writeValueAsString(requestDto);

        // Act + Assert
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        // Verify
        verify(studentService,never()).saveStudent(any(StudentRequestDto.class));
    }

    @Test
    void shouldReturn404WhenStudentDoesNotExist() throws Exception {

        // Arrange
        Long id= 99L;
        when(studentService.getStudentById(id)).thenThrow(new StudentNotFoundException(id));

        // Act + Assert
        mockMvc.perform(get("/api/students/{id}", id)).andExpect(status().isNotFound());

        // Verify
        verify(studentService).getStudentById(id);
    }

    @Test
    void shouldReturnStudentWhenIdExists() throws Exception {

        // Arrange
        Long id= 1L;
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", "rahul@gmail.com", 21);
        when(studentService.getStudentById(id)).thenReturn(responseDto);

        // Act + Assert
        mockMvc.perform(get("/api/students/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.age").value(21));

        // Verify
        verify(studentService).getStudentById(id);
    }

    @Test
    void shouldReturnAllStudentsSuccessfully() throws Exception {

        // Arrange
        StudentResponseDto responseDto1= new StudentResponseDto(1L,"Rahul","rahul@gmail.com",21);
        StudentResponseDto responseDto2= new StudentResponseDto(2L,"Amit","amit@gmail.com",23);
        List<StudentResponseDto> studentResponseDtoList= List.of(responseDto1,responseDto2);

        when(studentService.getAllStudents()).thenReturn(studentResponseDtoList);

        // Act + Assert
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Rahul"))
                .andExpect(jsonPath("$[0].email").value("rahul@gmail.com"))
                .andExpect(jsonPath("$[0].age").value(21))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Amit"))
                .andExpect(jsonPath("$[1].email").value("amit@gmail.com"))
                .andExpect(jsonPath("$[1].age").value(23));


        // Verify
        verify(studentService).getAllStudents();
    }

    @Test
    void shouldReturnEmptyListWhenNoStudentsExist() throws Exception {

        // Arrange
        when(studentService.getAllStudents()).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verify
        verify(studentService).getAllStudents();
    }

    @Test
    void shouldUpdateStudentSuccessfully() throws Exception {

        // Arrange
        Long id= 1L;
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21);
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", "rahulnew@gmail.com", 21);
        String requestJson= objectMapper.writeValueAsString(requestDto);
        when(studentService.updateStudent(eq(id), any(StudentRequestDto.class))).thenReturn(responseDto);

        // Act + Assert
        mockMvc.perform(put("/api/students/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email").value("rahulnew@gmail.com"))
                .andExpect(jsonPath("$.age").value(21));

        // Verify
        verify(studentService).updateStudent(eq(id), any(StudentRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingStudent() throws Exception {
        // Arrange
        Long id= 99L;
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21);
        String requestJson= objectMapper.writeValueAsString(requestDto);
        when(studentService.updateStudent(eq(id),any(StudentRequestDto.class))).thenThrow(new StudentNotFoundException(id));

        // Act + Assert
        mockMvc.perform(put("/api/students/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());

        // Verify
        verify(studentService).updateStudent(eq(id),any(StudentRequestDto.class));
    }

    @Test
    void shouldDeleteStudentSuccessfully() throws Exception {

        // Arrange
        Long id=1L;

        // Act + Assert
        mockMvc.perform(delete("/api/students/{id}", id)).andExpect(status().isOk())
                .andExpect(content().string("Student deleted Successfully"));

        // Verify
        verify(studentService).deleteStudent(id);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingStudent() throws Exception {

        // Arrange
        Long id = 99L;
        doThrow(new StudentNotFoundException(id)).when(studentService).deleteStudent(id);

        // Act + Assert
        mockMvc.perform(delete("/api/students/{id}", id)).andExpect(status().isNotFound());

        // Verify
        verify(studentService).deleteStudent(id);
    }
}
