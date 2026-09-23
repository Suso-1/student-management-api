package com.example.studentmanagement.dto;

import jakarta.validation.constraints.*;

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

    @NotNull(message = "Department Id is required")
    private Long departmentId;

    public StudentRequestDto(){}

    public StudentRequestDto(String name, String email, Integer age, String phoneNumber, Long departmentId){
        this.name=name;
        this.email=email;
        this.age=age;
        this.phoneNumber=phoneNumber;
        this.departmentId=departmentId;
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
