package com.example.studentmanagement.security;

import com.example.studentmanagement.entity.Department;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.repository.DepartmentRepository;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class SecurityIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4")
                    .withDatabaseName("student_management_security_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("db/create_initial_student_table.sql");

    private static final String ADMIN_USERNAME = "test-admin";
    private static final String ADMIN_PASSWORD = "AdminTest123!";
    private static final String STUDENT_USERNAME = "test-student";
    private static final String STUDENT_PASSWORD = "StudentTest123!";

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("jwt.secret", () ->
                "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdIUzI1NktleUZvclRlc3RzMTIzNDU2");
    }

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();

        userRepository.save(new User(
                null,
                ADMIN_USERNAME,
                passwordEncoder.encode(ADMIN_PASSWORD),
                "ADMIN"
        ));

        userRepository.save(new User(
                null,
                STUDENT_USERNAME,
                passwordEncoder.encode(STUDENT_PASSWORD),
                "STUDENT"
        ));

        Department department = departmentRepository.save(
                new Department(null, "Computer Science")
        );

        Student student = new Student(
                null,
                "Security Test Student",
                "security-test@example.com",
                21,
                "9876543210"
        );
        student.setDepartment(department);
        student.setRegisteredAt(Instant.now());

        studentRepository.save(student);
    }

    @Test
    void shouldReturn401WhenNoTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn200WhenValidTokenIsProvided() throws Exception {
        String token = loginAndGetToken(ADMIN_USERNAME, ADMIN_PASSWORD);

        assertFalse(token.isBlank());

        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WhenStudentTriesToUpdateStudent() throws Exception {
        String token = loginAndGetToken(STUDENT_USERNAME, STUDENT_PASSWORD);

        mockMvc.perform(put("/api/students/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Unauthorized Update",
                                    "email": "unauthorized@example.com",
                                    "age": 21,
                                    "phoneNumber": "9999999999",
                                    "departmentId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenStudentTriesToDeleteStudent() throws Exception {
        String token = loginAndGetToken(STUDENT_USERNAME, STUDENT_PASSWORD);

        mockMvc.perform(delete("/api/students/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401WhenInvalidTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken(String username, String password)
            throws Exception {

        String response = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "username": "%s",
                                        "password": "%s"
                                    }
                                    """.formatted(username, password))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();
    }
}