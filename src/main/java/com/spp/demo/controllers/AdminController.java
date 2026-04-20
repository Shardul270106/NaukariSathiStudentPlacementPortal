package com.spp.demo.controllers;


import com.spp.demo.dto.AdminLoginDto;
import com.spp.demo.models.Admin;
import com.spp.demo.repositories.AdminRepo;
import com.spp.demo.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.spp.demo.repositories.JobRepo;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins="*")
public class AdminController {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JobRepo jobRepo;

    // ================= REGISTER ADMIN =================
    @PostMapping("/register")
    public String register(@RequestBody Admin admin){

        Admin existing = adminRepo.findByStaffId(admin.getStaffId());

        if(existing == null){
            return "Invalid Staff ID. Contact college.";
        }

        if(existing.isEnabled()){
            return "Admin already registered";
        }

        existing.setName(admin.getName());
        existing.setEmail(admin.getEmail());
        existing.setPassword(admin.getPassword());

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        existing.setOtp(otp);
        existing.setOtpGeneratedTime(LocalDateTime.now());

        adminRepo.save(existing);

        emailService.sendOtpMail(
                existing.getEmail(),
                existing.getName(),
                otp
        );

        return "OTP Sent to Email";
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody Map<String,String> request){

        String staffId = request.get("staffId");
        String enteredOtp = request.get("otp");

        Admin admin = adminRepo.findByStaffId(staffId);

        if(admin == null){
            return "Admin not found";
        }

        if(admin.getOtpGeneratedTime()
                .plusMinutes(5)
                .isBefore(LocalDateTime.now())){
            return "OTP Expired";
        }

        if(!admin.getOtp().equals(enteredOtp)){

            admin.setOtpAttempts(admin.getOtpAttempts()+1);

            if(admin.getOtpAttempts()>=3){
                adminRepo.delete(admin);
                return "ADMIN_DELETED";
            }

            adminRepo.save(admin);
            return "Invalid OTP";
        }

        admin.setEnabled(true);
        admin.setOtp(null);
        admin.setOtpGeneratedTime(null);
        admin.setOtpAttempts(0);

        adminRepo.save(admin);

        return "Admin Verified Successfully";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody AdminLoginDto dto){

        Map<String,Object> response = new HashMap<>();

        Admin admin = adminRepo.findByStaffId(dto.getStaffId());

        if(admin == null){
            response.put("status","Admin not found");
            return response;
        }

        if(!admin.isEnabled()){
            response.put("status","Please verify email first");
            return response;
        }

        if(!admin.getPassword().equals(dto.getPassword())){
            response.put("status","Password incorrect");
            return response;
        }

        response.put("status","Admin login success");
        response.put("adminId",admin.getId());
        response.put("staffId",admin.getStaffId());

        return response;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long staffCount    = adminRepo.count();
        long activeJobs    = jobRepo.countByStatus("Active");
        long totalJobs     = jobRepo.count();
        long totalApplicants = jobRepo.sumApplicants(); // see below

        double placementRate = totalJobs > 0
                ? Math.round((activeJobs * 100.0) / totalJobs)
                : 94;

        stats.put("staffMembers", staffCount);
        stats.put("activeJobs",   activeJobs);
        stats.put("placementRate", placementRate);

        return stats;
    }

    @PostMapping("/add-staff")
    public String addStaff(@RequestBody Map<String, String> request) {
        if (!"SPP@Dev#2025!".equals(request.get("devPassword"))) {
            return "Unauthorized";
        }
        String staffId = request.get("staffId");
        if (adminRepo.findByStaffId(staffId) != null) {
            return "Staff ID already exists";
        }
        Admin admin = new Admin();
        admin.setStaffId(staffId);
        admin.setEnabled(false);
        adminRepo.save(admin);
        return "Staff ID added successfully";
    }
}