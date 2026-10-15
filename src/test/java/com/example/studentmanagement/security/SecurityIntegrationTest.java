package com.example.studentmanagement.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String username = System.getenv("TEST_USERNAME");
    private final String password = System.getenv("TEST_PASSWORD");

    @Test
    void shouldReturn401WhenNoTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn200WhenValidTokenIsProvided() throws Exception {
        String response= mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(username,password))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = new ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();

        assertFalse(token.isBlank());

        mockMvc.perform(get("/api/students").header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

   /*@Test
    void shouldReturn201WhenAdminCreatesStudent() throws Exception {
        String response = mockMvc.perform(
                post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(System.getenv("TEST_USERNAME"),
                                                System.getenv("TEST_PASSWORD")
                        ))
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = new ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();

        mockMvc.perform(post("/api/students")
                .header("Authorization", "Bearer "+ token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Security Admin Test",
                            "email": "security-admin-test@example.com",
                            "age": 21,
                            "phoneNumber": "8888888888",
                            "departmentId": 1
                        }
                        """)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }*/

    @Test

    void shouldReturn403WhenStudentTriesToUpdateStudent() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                    {
                                        "username": "%s",
                                        "password": "%s"
                                    }
                                    """.formatted(
                        System.getenv("STUDENT_USERNAME"),
                        System.getenv("STUDENT_PASSWORD")
                ))
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token= new ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();

        mockMvc.perform(put("/api/students/2")
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
                                    """)
        ).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenStudentTriesToDeleteStudent() throws Exception {

        String response = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "username": "%s",
                                        "password": "%s"
                                    }
                                    """.formatted(
                                        System.getenv("STUDENT_USERNAME"),
                                        System.getenv("STUDENT_PASSWORD")
                                ))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();

        mockMvc.perform(
                        delete("/api/students/2")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test

    void shouldReturn401WhenInvalidTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/students")
                .header("Authorization","Bearer invalid.jwt.token"))
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

        return new ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();
    }

}