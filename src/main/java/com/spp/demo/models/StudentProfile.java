package com.spp.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // ===== AI Extracted Personal =====

    private String address;

    // ===== AI Extracted Academic =====
    private String collegeName;
    private String branch;
    private String year;
    private String semester;
    private String cgpa;
    private String preferredField;

    // ===== AI Extracted Resume Content =====
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String projects;

    // ===== System Data =====
    private String resumeFilePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}