package com.example.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorTest {

    @Test
    void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "Student@123";

        String hash = encoder.encode(password);

        System.out.println(hash);
    }
}