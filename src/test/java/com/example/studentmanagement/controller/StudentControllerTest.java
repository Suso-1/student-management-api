package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDto;
import com.example.studentmanagement.dto.StudentResponseDto;
import com.example.studentmanagement.exception.StudentNotFoundException;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.studentmanagement.security.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldCreateStudentAndReturn201() throws Exception {

        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21, "9876543210",1L);
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", "rahul@gmail.com", 21, "9876543210",1L,"Computer Science");
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
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.departmentId").value(1))
                .andExpect(jsonPath("$.departmentName").value("Computer Science"));

        // Verify
        verify(studentService).saveStudent(any(StudentRequestDto.class));
    }

    @Test
    void shouldRejectInvalidStudentRequest() throws Exception {

        // Arrange
        StudentRequestDto requestDto= new StudentRequestDto("","wrongEmail",17, "9876543210",1L);
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
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", "rahul@gmail.com", 21, "9876543210",1L,"Computer Science");
        when(studentService.getStudentById(id)).thenReturn(responseDto);

        // Act + Assert
        mockMvc.perform(get("/api/students/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.departmentId").value(1))
                .andExpect(jsonPath("$.departmentName").value("Computer Science"));

        // Verify
        verify(studentService).getStudentById(id);
    }

    @Test
    void shouldReturnAllStudentsSuccessfully() throws Exception {

        // Arrange
        StudentResponseDto responseDto1 =
                new StudentResponseDto(1L, "Rahul", "rahul@gmail.com", 21, "9876543210", 1L, "Computer Science");

        StudentResponseDto responseDto2 =
                new StudentResponseDto(2L, "Amit", "amit@gmail.com", 23, "9123456780", 2L, "Information Tech");

        Page<StudentResponseDto> page =
                new PageImpl<>(
                        List.of(responseDto1, responseDto2),
                        PageRequest.of(0, 2), 2);

        when(studentService.getAllStudents(isNull(),any(Pageable.class)))
                .thenReturn(page);

        // Act + Assert
        mockMvc.perform(get("/api/students")
                        .param("page", "0").param("size", "2"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content.length()").value(2))

                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Rahul"))
                .andExpect(jsonPath("$.content[0].email").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.content[0].age").value(21))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.content[0].departmentId").value(1))
                .andExpect(jsonPath("$.content[0].departmentName").value("Computer Science"))

                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Amit"))
                .andExpect(jsonPath("$.content[1].email").value("amit@gmail.com"))
                .andExpect(jsonPath("$.content[1].age").value(23))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("9123456780"))
                .andExpect(jsonPath("$.content[1].departmentId").value(2))
                .andExpect(jsonPath("$.content[1].departmentName").value("Information Tech"))

//                .andExpect(jsonPath("$.totalElements").value(2))
//                .andExpect(jsonPath("$.totalPages").value(1))
//                .andExpect(jsonPath("$.number").value(0))
//                .andExpect(jsonPath("$.size").value(2));
        .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(studentService).getAllStudents(isNull(),any(Pageable.class));
    }

    @Test
    void shouldReturnStudentsByDepartment() throws Exception {

        // Arrange
        StudentResponseDto responseDto1 =
                new StudentResponseDto(1L, "Rahul", "rahul@gmail.com", 21, "9876543210", 1L, "Computer Science");

        StudentResponseDto responseDto2 = new StudentResponseDto(2L, "Amit", "amit@gmail.com", 22, "9123456780", 1L, "Computer Science");

        Page<StudentResponseDto> page =
                new PageImpl<>(
                        List.of(responseDto1, responseDto2), PageRequest.of(0, 2), 2);

        when(studentService.getAllStudents(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act + Assert
        mockMvc.perform(
                        get("/api/students")
                                .param("departmentId", "1")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Rahul"))
                .andExpect(jsonPath("$.content[0].departmentId").value(1))
                .andExpect(jsonPath("$.content[1].name").value("Amit"))
                .andExpect(jsonPath("$.content[1].departmentId").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));

        // Verify
        verify(studentService).getAllStudents(eq(1L), any(Pageable.class));
    }
    @Test
    void shouldReturnEmptyPageWhenNoStudentsExist() throws Exception {

        Page<StudentResponseDto> emptyPage =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(studentService.getAllStudents(isNull(),any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/students")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(studentService).getAllStudents(isNull(),any(Pageable.class));
    }

    @Test
    void shouldUpdateStudentSuccessfully() throws Exception {

        // Arrange
        Long id= 1L;
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21, "9876543210",1L);
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", "rahulnew@gmail.com", 21, "9876543210",1L,"Computer Science");
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
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.departmentId").value(1))
                .andExpect(jsonPath("$.departmentName").value("Computer Science"));

        // Verify
        verify(studentService).updateStudent(eq(id), any(StudentRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingStudent() throws Exception {
        // Arrange
        Long id= 99L;
        StudentRequestDto requestDto = new StudentRequestDto("Rahul", "rahul@gmail.com", 21, "9876543210",1L);
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

    @Test
    void shouldRejectRequestWhenDepartmentIdIsMissing() throws Exception {
        String requestJson = """
                {"name": "Rahul","email": "rahul@gmail.com","age": 21,"phoneNumber": "9876543210"}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message")
                            .value("Validation failed"))
                        .andExpect(jsonPath("$.errors.departmentId")
                            .value("Department ID is required"));

        verify(studentService, never())
                        .saveStudent(any(StudentRequestDto.class));
    }

    @Test
    void shouldReturnStudentWhenEmailExists() throws Exception {
        String email="rahul@gmail.com";
        StudentResponseDto responseDto= new StudentResponseDto(1L,"Rahul", email, 21, "9876543210",1L,"Computer Science");
        when(studentService.getStudentByEmail(email)).thenReturn(responseDto);

        mockMvc.perform(get("/api/students/email/rahul@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value(email))
                .andExpect(jsonPath("$.departmentId")
                        .value(1));
    }

    @Test
    void shouldReturn404WhenEmailDoesNotExist() throws Exception {

        when(studentService.getStudentByEmail("unknown@gmail.com"))
                .thenThrow(new StudentNotFoundException("Student not found with email: unknown@gmail.com"));

        mockMvc.perform(get("/api/students/email/unknown@gmail.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                .value("Student not found with email: unknown@gmail.com"));
    }

    @Test
    void shouldSupportSortingStudentsByName() throws Exception {

        StudentResponseDto responseDto1 =
                new StudentResponseDto(1L, "Amit", "amit@gmail.com", 22, "9876543211", 1L, "Computer Science");

        StudentResponseDto responseDto2 =
                new StudentResponseDto(2L, "Rahul", "rahul@gmail.com", 21, "9876543210", 1L, "Computer Science");

        Page<StudentResponseDto> page =
                new PageImpl<>(List.of(responseDto1, responseDto2),
                        PageRequest.of(0, 2, Sort.by("name").ascending()), 2);

        when(studentService.getAllStudents(isNull(),any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/students")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Amit"))
                .andExpect(jsonPath("$.content[1].name").value("Rahul"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(studentService).getAllStudents(isNull(),any(Pageable.class));
    }

//    @Test
//    void generatePasswordHash() {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        System.out.println(
//                encoder.encode("admin123")
//        );
//    }
}
