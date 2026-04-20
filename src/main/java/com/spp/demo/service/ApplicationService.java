package com.spp.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spp.demo.models.*;
import com.spp.demo.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ApplicationService {

    @Autowired private UserRepo               userRepo;
    @Autowired private JobRepo                jobRepo;
    @Autowired private StudentProfileRepository profileRepo;
    @Autowired private JobApplicationRepository applicationRepo;
    @Autowired private AiMatchScoreService    aiMatchScoreService;
    @Autowired private EmailService           emailService;

    private final ObjectMapper mapper = new ObjectMapper();

    // ────────────────────────────────────────────────────────────────────────
    // APPLY
    // ────────────────────────────────────────────────────────────────────────
    public String apply(Integer userId, Long jobId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        StudentProfile profile = user.getStudentProfile();
        if (profile == null) {
            return "Please upload resume first";
        }

        // ── Prevent duplicate apply ──
        if (applicationRepo.findByUserIdAndJobId(userId, jobId).isPresent()) {
            return "You already applied for this job";
        }

        // ── CGPA check ──
        double studentCgpa = Double.parseDouble(profile.getCgpa());
        if (studentCgpa < job.getCgpa()) {
            return "Not eligible: CGPA requirement not met";
        }

        // ── Branch check ──
        if (!profile.getBranch().equalsIgnoreCase(job.getBranch())) {
            return "Not eligible: Branch mismatch";
        }

        // ── Basic skill check ──
        List<String> studentSkillList =
                Arrays.stream(profile.getSkills().toLowerCase().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toList());

        List<String> jobSkillList =
                Arrays.stream(job.getSkills().toLowerCase().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toList());

        boolean skillMatch = jobSkillList.stream().anyMatch(jobSkill ->
                studentSkillList.stream().anyMatch(studentSkill ->
                        studentSkill.contains(jobSkill) || jobSkill.contains(studentSkill)
                )
        );

        if (!skillMatch) {
            return "Not eligible: Required skills missing";
        }

        // ── AI Match Score ──────────────────────────────────────────────────
        int    aiScore       = 0;
        String matchedSkills = "[]";
        String missingSkills = "[]";

        // Build the canonical job-skill set (lowercase, trimmed) for safety validation
        Set<String> canonicalJobSkills = jobSkillList.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        try {
            String aiResult = aiMatchScoreService.calculateMatch(
                    job.getSkills(),
                    profile.getSkills()
            );

            System.out.println("AI RESULT: " + aiResult);

            JsonNode node = mapper.readTree(aiResult);

            // ── SAFETY GUARD: strip any skills the AI invented ─────────────
            // Only keep skill names that actually exist in the job's skill list
            List<String> rawMatched = toStringList(node.get("matchedSkills"));
            List<String> rawMissing = toStringList(node.get("missingSkills"));

            List<String> safeMatched = rawMatched.stream()
                    .filter(s -> canonicalJobSkills.contains(s.toLowerCase().trim()))
                    .collect(Collectors.toList());

            List<String> safeMissing = rawMissing.stream()
                    .filter(s -> canonicalJobSkills.contains(s.toLowerCase().trim()))
                    .collect(Collectors.toList());

            // Recalculate score from sanitised lists (don't trust AI's number blindly)
            int totalJobSkills = canonicalJobSkills.size();
            aiScore = totalJobSkills == 0 ? 100
                    : (int) Math.round((safeMatched.size() * 100.0) / totalJobSkills);

            matchedSkills = mapper.writeValueAsString(safeMatched);
            missingSkills = mapper.writeValueAsString(safeMissing);

        } catch (Exception e) {
            e.printStackTrace();
            // On AI failure: fall back to 0 score, keep application going
            aiScore       = 0;
            matchedSkills = "[]";
            // Build a safe JSON array string from jobSkillList without risking another exception
            missingSkills = jobSkillList.isEmpty() ? "[]"
                    : "[\"" + String.join("\",\"", jobSkillList) + "\"]";
        }

        // ── Save application ────────────────────────────────────────────────
        JobApplication app = new JobApplication();
        app.setUserId(userId);
        app.setJobId(jobId);
        app.setStatus("APPLIED");
        app.setAiScore(aiScore);
        app.setMatchedSkills(matchedSkills);
        app.setMissingSkills(missingSkills);
        app.setAppliedAt(LocalDateTime.now());
        applicationRepo.save(app);

        // Increment applicant count
        job.setApplicants(job.getApplicants() + 1);
        jobRepo.save(job);

        return "Application submitted successfully. AI Score: " + aiScore + "%";
    }

    // ────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ────────────────────────────────────────────────────────────────────────

    /** Safely converts a JsonNode array to a List<String>. */
    private List<String> toStringList(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) return Collections.emptyList();
        return StreamSupport.stream(arrayNode.spliterator(), false)
                .map(JsonNode::asText)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());
    }

    // ────────────────────────────────────────────────────────────────────────
    // OTHER METHODS (unchanged logic, minor cleanup)
    // ────────────────────────────────────────────────────────────────────────

    public List<JobApplication> getApplicationsByJob(Long jobId) {
        return applicationRepo.findByJobId(jobId);
    }

    public void updateStatus(Long applicationId, String status) {
        JobApplication app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        applicationRepo.save(app);

        User student = userRepo.findById(app.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ACCEPTED".equals(status)) {
            emailService.sendApplicationAcceptedMail(student.getEmail(), student.getName());
        } else {
            emailService.sendApplicationRejectedMail(student.getEmail(), student.getName());
        }
    }

    public List<JobApplication> getApplicationsByUser(Integer userId) {
        return applicationRepo.findByUserId(userId);
    }

    public void deleteApplication(Long applicationId) {
        applicationRepo.deleteById(applicationId);
    }
}
