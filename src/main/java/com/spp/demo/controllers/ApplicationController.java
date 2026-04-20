package com.spp.demo.controllers;



import com.spp.demo.dto.ApplicationStatusDto;
import com.spp.demo.models.JobApplication;
import com.spp.demo.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
@CrossOrigin
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply")
    public String applyJob(@RequestParam Integer userId,
                           @RequestParam Long jobId){

        return applicationService.apply(userId,jobId);
    }

    // ADMIN: Get applications for job
    @GetMapping("/job/{jobId}")
    public List<JobApplication> getApplicationsByJob(@PathVariable Long jobId){
        return applicationService.getApplicationsByJob(jobId);
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestBody Map<String,Object> data){

        Long applicationId = Long.valueOf(data.get("applicationId").toString());
        String status = data.get("status").toString();

        applicationService.updateStatus(applicationId,status);

        return "Application " + status + " successfully";
    }
    @GetMapping("/student/{userId}")
    public List<JobApplication> getStudentApplications(@PathVariable Integer userId){
        return applicationService.getApplicationsByUser(userId);
    }

    @DeleteMapping("/delete/{applicationId}")
    public String deleteApplication(@PathVariable Long applicationId){
        applicationService.deleteApplication(applicationId);
        return "Application deleted successfully";
    }
}