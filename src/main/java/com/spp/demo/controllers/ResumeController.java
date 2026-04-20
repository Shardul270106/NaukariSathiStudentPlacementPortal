package com.spp.demo.controllers;

import com.spp.demo.models.StudentProfile;
import com.spp.demo.models.User;
import com.spp.demo.repositories.UserRepo;
import com.spp.demo.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/resume")
@CrossOrigin("*")
public class ResumeController {

    @Autowired
    private ResumeParserService resumeParserService;

    @Autowired
    private UserRepo userRepo;

    // Upload — unchanged
    @PostMapping("/upload/{userId}")
    public StudentProfile uploadResume(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        return resumeParserService.parseAndSaveResume(userId, file);
    }

    // View — now returns Cloudinary URL instead of file bytes
    @PostMapping("/view")
    public ResponseEntity<byte[]> viewResume(@RequestBody Map<String, Integer> body) {

        Integer userId = body.get("userId");
        if (userId == null) return ResponseEntity.badRequest().build();

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        StudentProfile profile = user.getStudentProfile();
        if (profile == null || profile.getResumeFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // Fetch PDF bytes from Cloudinary URL
            org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
            byte[] pdfBytes = rt.getForObject(profile.getResumeFilePath(), byte[].class);

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=\"resume.pdf\"")
                    .header("Access-Control-Allow-Origin", "*")
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}

