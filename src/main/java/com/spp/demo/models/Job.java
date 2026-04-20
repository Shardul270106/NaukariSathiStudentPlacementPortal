package com.spp.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import jakarta.persistence.Table;
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;

    private String companyName;
    @Column(length = 2000)
    private String jobDescription;

    private String jobCategory;

    private String salary;

    // Eligibility
    private Double cgpa;
    private String year;
    private String skills;
    private String branch;

    // admin reference
    private Long adminId;

    private String companyLogo;


    public String getCompanyLogo() {
        return companyLogo;
    }
    private String status = "Active";

    private Integer applicants = 0;

    private LocalDateTime createdDate = LocalDateTime.now();




    public Job(){}


    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }


    public Long getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle)
    {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getApplicants() { return applicants; }
    public void setApplicants(Integer applicants) { this.applicants = applicants; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}