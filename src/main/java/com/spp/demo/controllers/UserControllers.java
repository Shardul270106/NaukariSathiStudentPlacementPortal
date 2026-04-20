package com.spp.demo.controllers;

import com.spp.demo.dto.LoginDto;
import com.spp.demo.models.StudentProfile;
import com.spp.demo.models.User;
import com.spp.demo.repositories.StudentProfileRepository;
import com.spp.demo.repositories.UserRepo;
import com.spp.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spp.demo.repositories.JobRepo;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*")
public class UserControllers {

    // ── Validation patterns ──────────────────────────────────────────────────
    // MU Enrollment: starts with "MU" followed by exactly 16 digits → 18 chars total
    private static final Pattern MU_PATTERN = Pattern.compile("^MU\\d{16}$");

    // ABC ID: exactly 12 digits
    private static final Pattern ABC_PATTERN = Pattern.compile("^\\d{12}$");

    // Password rules: 8+ chars, at least one uppercase, one lowercase, one digit, one special char
    private static final Pattern PWD_UPPER   = Pattern.compile("[A-Z]");
    private static final Pattern PWD_LOWER   = Pattern.compile("[a-z]");
    private static final Pattern PWD_DIGIT   = Pattern.compile("\\d");
    private static final Pattern PWD_SPECIAL = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]");

    @Autowired UserRepo userRepo;
    @Autowired EmailService emailService;
    @Autowired private StudentProfileRepository studentProfileRepository;
    @Autowired private JobRepo jobRepository;

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Validates a password against all requirements.
     * Returns null if valid, or an error message string if invalid.
     */
    private String validatePassword(String password) {
        if (password == null || password.length() < 8)
            return "ERROR: Password must be at least 8 characters long.";
        if (!PWD_UPPER.matcher(password).find())
            return "ERROR: Password must contain at least one uppercase letter (A-Z).";
        if (!PWD_LOWER.matcher(password).find())
            return "ERROR: Password must contain at least one lowercase letter (a-z).";
        if (!PWD_DIGIT.matcher(password).find())
            return "ERROR: Password must contain at least one digit (0-9).";
        if (!PWD_SPECIAL.matcher(password).find())
            return "ERROR: Password must contain at least one special character (!@#$%^&* …).";
        return null; // ✅ all good
    }

    // ── Endpoints ────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        // 1. Validate MU Enrollment Number
        if (user.getMuenrollmentid() == null || !MU_PATTERN.matcher(user.getMuenrollmentid()).matches()) {
            return "ERROR: Invalid MU Enrollment Number. Must start with 'MU' followed by 16 digits (e.g. MU0341120240199116).";
        }

        // 2. Validate ABC ID
        if (user.getAbcid() == null || !ABC_PATTERN.matcher(user.getAbcid()).matches()) {
            return "ERROR: Invalid ABC ID. Must be exactly 12 digits (e.g. 578785817252).";
        }

        // 3. Validate Password strength
        String pwdError = validatePassword(user.getPassword());
        if (pwdError != null) {
            return pwdError;
        }

        // 4. Check duplicate username
        User existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null) {
            if (existingUser.isEnabled()) {
                return "ERROR: Username already exists.";
            } else {
                userRepo.delete(existingUser); // remove unverified stale account
            }
        }

        // 5. Check other unique fields
        if (userRepo.findByCollegeid(user.getCollegeid()) != null)
            return "ERROR: College ID already exists.";

        if (userRepo.findByMuenrollmentid(user.getMuenrollmentid()) != null)
            return "ERROR: MU Enrollment Number already registered.";

        if (userRepo.findByAbcid(user.getAbcid()) != null)
            return "ERROR: ABC ID already registered.";

        // 6. Generate OTP and save user
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        user.setEnabled(false);
        user.setOtpAttempts(0);

        User savedUser = userRepo.save(user);

        emailService.sendOtpMail(savedUser.getEmail(), savedUser.getName(), otp);

        return "OTP Sent to your Email 📩";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody Map<String, String> request) {

        String username   = request.get("username");
        String enteredOtp = request.get("otp");

        User user = userRepo.findByUsername(username);

        if (user == null)
            return "User not found.";

        if (user.getOtp() == null)
            return "No OTP found. Please register again.";

        // OTP expiry check (5 minutes)
        if (user.getOtpGeneratedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            userRepo.delete(user);
            return "OTP Expired ⏳ Please register again.";
        }

        // Wrong OTP
        if (!user.getOtp().equals(enteredOtp)) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            if (user.getOtpAttempts() >= 3) {
                userRepo.delete(user);
                return "ACCOUNT_DELETED";
            }
            userRepo.save(user);
            return "Invalid OTP ❌ Attempts left: " + (3 - user.getOtpAttempts());
        }

        // ✅ OTP correct
        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        user.setOtpAttempts(0);
        userRepo.save(user);

        emailService.sendRegistrationMail(user.getEmail(), user.getName());

        return "Email Verified Successfully ✅";
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDto u) {

        Map<String, Object> response = new java.util.HashMap<>();

        User user = userRepo.findByUsername(u.getUsername());

        if (user == null) {
            response.put("status", "User not found.");
            return response;
        }

        if (!user.isEnabled()) {
            response.put("status", "Please verify your email first 🔐");
            return response;
        }

        if (!u.getPassword().equals(user.getPassword())) {
            response.put("status", "Password Incorrect ❌");
            return response;
        }

        response.put("status",   "Login success");
        response.put("userId",   user.getId());
        response.put("username", user.getUsername());
        return response;
    }

    @GetMapping("/api/user/{id}")
    public Map<String, Object> getUserById(@PathVariable int id) {

        Map<String, Object> response = new java.util.HashMap<>();

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            response.put("error", "User not found.");
            return response;
        }

        response.put("name",      user.getName());
        response.put("email",     user.getEmail());
        response.put("contact",   user.getContact());
        response.put("collegeId", user.getCollegeid());

        Optional<StudentProfile> optionalProfile = studentProfileRepository.findByUser(user);
        if (optionalProfile.isPresent()) {
            StudentProfile profile = optionalProfile.get();
            response.put("address",        profile.getAddress());
            response.put("collegeName",    profile.getCollegeName());
            response.put("branch",         profile.getBranch());
            response.put("year",           profile.getYear());
            response.put("semester",       profile.getSemester());
            response.put("cgpa",           profile.getCgpa());
            response.put("preferredField", profile.getPreferredField());

            String path = System.getProperty("user.dir")
                    + File.separator + "uploads"
                    + File.separator + "resumes"
                    + File.separator + user.getId() + ".pdf";
            response.put("resumeUploaded", new File(path).exists());
        }

        return response;
    }


    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("recruiters",    jobRepository.countDistinctCompanies());
        stats.put("placementRate", 98);
        stats.put("avgPackage",    jobRepository.findAverageSalary());
        return stats;
    }
}
