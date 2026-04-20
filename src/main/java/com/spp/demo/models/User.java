package com.spp.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;
    @Column(nullable = false,unique = true)
    String collegeid;
    @Column(nullable = false,unique = true)
    String muenrollmentid;
    @Column(nullable = false,unique = true)
    String abcid;
    @Column(nullable = false,unique = true)
    String email;
    @Column(nullable = false)
    String contact;
    @Column(nullable = false,unique = true)
    String username;
    @Column(nullable = false)
    String password;

    @Column(length = 6)
    private String otp;

    private LocalDateTime otpGeneratedTime;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = false)
    private int otpAttempts = 0;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private StudentProfile studentProfile;
}



