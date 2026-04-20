package com.spp.demo.service;

import com.spp.demo.models.Job;
import com.spp.demo.models.JobApplication;
import com.spp.demo.models.PlacementRecord;
import com.spp.demo.repositories.JobApplicationRepository;
import com.spp.demo.repositories.JobRepo;
import com.spp.demo.repositories.PlacementRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlacementService {

    @Autowired
    private PlacementRecordRepository repo;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private JobApplicationRepository applicationRepo;
    @Transactional
    public PlacementRecord saveRecord(PlacementRecord record) {

        if ("ON_CAMPUS".equalsIgnoreCase(record.getPlacementType())
                && record.getUserId() != null
                && record.getCompanyName() != null) {

            List<Job> jobs = jobRepo.findByCompanyName(record.getCompanyName());

            for (Job job : jobs) {
                applicationRepo.findByUserIdAndJobId(record.getUserId(), job.getId())
                        .ifPresent(application -> {
                            application.setStatus("ACCEPTED");
                            applicationRepo.save(application);
                        });
            }
        }

        return repo.save(record);
    }

    public List<PlacementRecord> getAllRecords() {
        return repo.findAll();
    }

    public List<PlacementRecord> getByUser(Integer userId) {
        return repo.findByUserId(userId);
    }
}