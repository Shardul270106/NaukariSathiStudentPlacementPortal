package com.spp.demo.repositories;

import com.spp.demo.models.StudentProfile;
import com.spp.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {


    Optional<StudentProfile> findByUser(User user);
}

