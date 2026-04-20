package com.spp.demo.dto;


import lombok.Data;

@Data
public class StudentAdminDto {

    private int userId;
    private String name;
    private String email;
    private String contact;

    private String collegeName;
    private String branch;
    private String year;
    private String cgpa;

    private String resumePath;
}
