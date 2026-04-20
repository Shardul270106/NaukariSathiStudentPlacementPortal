package com.spp.demo.controllers;


import com.spp.demo.models.Job;
import com.spp.demo.models.JobApplication;
import com.spp.demo.models.User;
import com.spp.demo.repositories.UserRepo;
import com.spp.demo.service.ApplicationService;
import com.spp.demo.service.CloudinaryService;
import com.spp.demo.service.EmailService;
import com.spp.demo.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins="*")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ADD JOB

    @PostMapping("/add")
    public Job addJob(
            @RequestParam String jobTitle,
            @RequestParam String companyName,
            @RequestParam String jobDescription,
            @RequestParam String jobCategory,
            @RequestParam String salary,
            @RequestParam Double cgpa,
            @RequestParam String year,
            @RequestParam String skills,
            @RequestParam String branch,
            @RequestParam Long adminId,
            @RequestParam("logo") MultipartFile logo
    ) {

        try {

            String logoUrl = cloudinaryService.uploadImage(
                    logo,
                    "logo_" + companyName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis()
            );

            Job job = new Job();

            job.setJobTitle(jobTitle);
            job.setCompanyName(companyName);
            job.setJobDescription(jobDescription);
            job.setJobCategory(jobCategory);
            job.setSalary(salary);
            job.setCgpa(cgpa);
            job.setYear(year);
            job.setSkills(skills);
            job.setBranch(branch);
            job.setAdminId(adminId);
            job.setCompanyLogo(logoUrl);

            Job savedJob = jobService.addJob(job);

            // Send job emails
            List<User> students = userRepo.findByEnabledTrue();

            for(User student : students){

                emailService.sendNewJobNotification(
                        student.getEmail(),
                        student.getName(),
                        job.getCompanyName(),
                        job.getJobTitle(),
                        job.getJobCategory(),
                        job.getSalary(),
                        job.getCgpa(),
                        job.getYear(),
                        job.getBranch(),
                        job.getSkills(),
                        job.getJobDescription(),
                        job.getCompanyLogo()
                );
            }

            return savedJob;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    // GET ALL JOBS
    @GetMapping("/all")
    public List<Job> getAllJobs(){
        return jobService.getAllJobs();
    }

    // GET JOBS BY ADMIN
    @GetMapping("/admin/{adminId}")
    public List<Job> getJobsByAdmin(@PathVariable Long adminId){
        return jobService.getJobsByAdmin(adminId);
    }

    // DELETE JOB
    @DeleteMapping("/delete/{id}")
    public String deleteJob(@PathVariable Long id){
        jobService.deleteJob(id);
        return "Job deleted successfully";
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id){
        return jobService.getJobById(id);
    }

    // UPDATE JOB
    @PutMapping("/update/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job updatedJob){

        Job job = jobService.getJobById(id);

        job.setJobTitle(updatedJob.getJobTitle());
        job.setJobDescription(updatedJob.getJobDescription());
        job.setJobCategory(updatedJob.getJobCategory());
        job.setSalary(updatedJob.getSalary());
        job.setCgpa(updatedJob.getCgpa());
        job.setYear(updatedJob.getYear());
        job.setSkills(updatedJob.getSkills());
        job.setBranch(updatedJob.getBranch());

        return jobService.addJob(job);
    }


    // CLOSE JOB
    @PutMapping("/close/{id}")
    public Job closeJob(@PathVariable Long id){

        Job job = jobService.getJobById(id);
        job.setStatus("Closed");

        return jobService.addJob(job);
    }

    @PutMapping("/activate/{id}")
    public Job activateJob(@PathVariable Long id){

        Job job = jobService.getJobById(id);

        job.setStatus("Active");

        return jobService.addJob(job);
    }

    @GetMapping("/active")
    public List<Job> getActiveJobs(){
        return jobService.getActiveJobs();
    }

    @GetMapping("/active/student/{userId}")
    public List<Job> getActiveJobsForStudent(@PathVariable Integer userId) {
        return jobService.getActiveJobsForStudent(userId);
    }

}

