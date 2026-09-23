package com.example.studentmanagement.dto;

public class StudentResponseDto {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String phoneNumber;
    private Long departmentId;
    private String departmentName;

    public StudentResponseDto(){}

    public StudentResponseDto(Long id, String name, String email, Integer age, String phoneNumber, Long departmentId, String departmentName){
        this.id=id;
        this.name=name;
        this.email=email;
        this.age=age;
        this.phoneNumber=phoneNumber;
        this.departmentId=departmentId;
        this.departmentName=departmentName;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDepartmentId() {return departmentId;}

    public void setDepartmentId(Long departmentId) {this.departmentId = departmentId;}

    public String getDepartmentName() {return departmentName;}

    public void setDepartmentName(String departmentName) {this.departmentName = departmentName;}



}
