package com.example.studentmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class StudentRequestDto {

    @NotBlank(message = "name cannot be blank ")
    private String name;


    @Email
    @NotBlank (message = "provide a valid email address ")
    private String email;

    @Min(18)
    @Max(value=100, message = "Age must not exceed 100")
    private Integer age;

    private String phoneNumber;

    public StudentRequestDto(){}

    public StudentRequestDto(String name, String email, Integer age, String phoneNumber){
        this.name=name;
        this.email=email;
        this.age=age;
        this.phoneNumber=phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
