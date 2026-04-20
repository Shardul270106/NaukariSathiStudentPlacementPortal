package com.spp.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "placement_records")
public class PlacementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // if student exists in system
    private Integer userId;

    // for off campus student
    private String studentName;
    private String collegeId;

    private String companyName;

    private String jobRole;

    private Double packageLpa;

    private String placementType; // ON_CAMPUS or OFF_CAMPUS

    private LocalDate offerDate;
}
