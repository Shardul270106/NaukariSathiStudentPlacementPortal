package com.spp.demo.repositories;


import com.spp.demo.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Long> {

    List<Job> findByAdminId(Long adminId);
    List<Job> findByStatus(String status);
    List<Job> findByCompanyName(String companyName);

    // Latest jobs (LIMIT → performance boost)
    List<Job> findTop10ByOrderByIdDesc();

    // Case-insensitive company search
    List<Job> findByCompanyNameContainingIgnoreCase(String company);

    // Skill-based filtering
    List<Job> findBySkillsContainingIgnoreCase(String skill);

    // Branch-based filtering
    List<Job> findByBranchContainingIgnoreCase(String branch);

    // Combined filter (VERY POWERFUL)
    List<Job> findByCompanyNameContainingIgnoreCaseAndSkillsContainingIgnoreCase(String company, String skill);

    long countByStatus(String active);

    @Query("SELECT j FROM Job j WHERE j.status = 'Active' AND j.id NOT IN " +
            "(SELECT a.jobId FROM JobApplication a WHERE a.userId = :userId AND a.status = 'ACCEPTED')")
    List<Job> findActiveJobsExcludingAccepted(@Param("userId") Integer userId);

    @Query("SELECT COUNT(DISTINCT j.companyName) FROM Job j WHERE j.status = 'Active'")
    long countDistinctCompanies();

    @Query("SELECT AVG(CAST(j.salary AS double)) FROM Job j WHERE j.status = 'Active'")
    Double findAverageSalary();

    @Query("SELECT COALESCE(SUM(j.applicants), 0) FROM Job j")
    long sumApplicants();
}