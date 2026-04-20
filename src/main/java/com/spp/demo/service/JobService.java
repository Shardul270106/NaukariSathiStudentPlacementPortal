package com.spp.demo.service;



import com.spp.demo.models.Job;
import com.spp.demo.repositories.JobApplicationRepository;
import com.spp.demo.repositories.JobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private JobApplicationRepository applicationRepo; // add this

    public List<Job> getActiveJobsForStudent(Integer userId) {
        return jobRepo.findActiveJobsExcludingAccepted(userId);
    }

    public Job addJob(Job job){
        return jobRepo.save(job);
    }

    public List<Job> getAllJobs(){
        return jobRepo.findAll();
    }

    public List<Job> getJobsByAdmin(Long adminId){
        return jobRepo.findByAdminId(adminId);
    }

    public void deleteJob(Long id){
        jobRepo.deleteById(id);
    }

    public Job getJobById(Long id){
        return jobRepo.findById(id).orElse(null);
    }
    public List<Job> getActiveJobs(){
        return jobRepo.findByStatus("Active");
    }
}
