package com.spp.demo.dto;


import lombok.Data;

@Data
public class ResumeDTO {

    private String name;
    private String email;
    private String phone;
    private String address;

    private String collegeName;
    private String branch;
    private String cgpa;

    private String summary;
    private String skills;
    private String education;
    private String experience;
    private String projects;
}