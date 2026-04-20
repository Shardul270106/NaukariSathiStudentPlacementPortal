package com.spp.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;

    private Long jobId;

    private String status;

    private Integer aiScore;

    private LocalDateTime appliedAt;

    private String matchedSkills;

    private String missingSkills;

    // getters setters
}