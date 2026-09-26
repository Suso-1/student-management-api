package com.example.studentmanagement.dto;
public record LoginResponse(
        String message,
        String username,
        String token
) {
}