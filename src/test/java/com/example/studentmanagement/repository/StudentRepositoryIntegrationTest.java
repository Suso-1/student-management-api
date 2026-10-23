package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Department;
import com.example.studentmanagement.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class StudentRepositoryIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4")
                    .withDatabaseName("student_management_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("db/create_initial_student_table.sql");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("jwt.secret", () ->
                "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdIUzI1NktleUZvclRlc3RzMTIzNDU2"
        );
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void shouldPersistAndRetrieveStudent() {
        Department department = departmentRepository.findById(1L)
                .orElseThrow(()-> new AssertionError("Department with Id1 was not found"));
        Student student = new Student(null,"Integration Test Student", "integration-test@example.com", 21, "9876543210");
        student.setDepartment(department);
        student.setRegisteredAt(Instant.now());
        Student savedStudent= studentRepository.saveAndFlush(student);
        Student retrievedStudent= studentRepository.findById(savedStudent.getId())
                .orElseThrow(()->new AssertionError("Saved student was not found"));

        assertThat(savedStudent.getId()).isNotNull();
        assertThat(retrievedStudent.getName()).isEqualTo("Integration Test Student");
        assertThat(retrievedStudent.getEmail()).isEqualTo("integration-test@example.com");
        assertThat(retrievedStudent.getDepartment().getId()).isEqualTo(department.getId());
        assertThat(retrievedStudent.getRegisteredAt()).isNotNull();
    }
}