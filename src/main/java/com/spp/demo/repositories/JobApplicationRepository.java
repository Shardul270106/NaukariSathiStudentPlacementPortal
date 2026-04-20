package com.spp.demo.repositories;

import com.spp.demo.models.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {

    Optional<JobApplication> findByUserIdAndJobId(Integer userId, Long jobId);
    List<JobApplication> findByJobId(Long jobId);
    void deleteByUserIdAndJobId(Integer userId, Long jobId);

    List<JobApplication> findByUserId(Integer userId);

}