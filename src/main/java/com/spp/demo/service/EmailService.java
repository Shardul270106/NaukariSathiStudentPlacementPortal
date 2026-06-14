package com.spp.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String FROM_EMAIL = "aea802001@smtp-brevo.com";
    private static final String FROM_NAME = "NaukariSathi Portal";

    private void sendEmail(String toEmail, String toName, String subject, String htmlContent) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = Map.of(
                "sender", Map.of("name", FROM_NAME, "email", FROM_EMAIL),
                "to", new Object[]{Map.of("email", toEmail, "name", toName)},
                "subject", subject,
                "htmlContent", htmlContent
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(BREVO_URL, request, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendOtpMail(String toEmail, String name, String otp) {
        String html =
            "<div style='font-family:Arial;text-align:center;padding:20px;'>"
            + "<h2>Email Verification Required 🔐</h2>"
            + "<p>Hi <b>" + name + "</b>,</p>"
            + "<p>Your OTP for email verification is:</p>"
            + "<h1 style='color:#2E86C1;font-size:36px;'>" + otp + "</h1>"
            + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
            + "<small>If you did not register, please ignore this email.</small>"
            + "</div>";
        sendEmail(toEmail, name, "OTP Verification - NaukariSathi 🔐", html);
    }

    @Async
    public void sendRegistrationMail(String toEmail, String name) {
        String html =
            "<div style='font-family:Arial;text-align:center;padding:20px;'>"
            + "<h2>Welcome to NaukariSathi 🚀</h2>"
            + "<p>Hi <b>" + name + "</b>,</p>"
            + "<p>Your registration was successful.</p>"
            + "<ul style='list-style:none;'>"
            + "<li>✔ Apply for campus drives</li>"
            + "<li>✔ View company postings</li>"
            + "<li>✔ Track application status</li>"
            + "</ul>"
            + "<small>Placement Cell - RGIT</small>"
            + "</div>";
        sendEmail(toEmail, name, "Welcome to NaukariSathi 🎓", html);
    }

    @Async
    public void sendNewJobNotification(String toEmail, String name, String companyName,
            String jobTitle, String category, String salary, Double cgpa,
            String year, String branch, String skills, String description, String logoFile) {
        String html =
            "<div style='font-family:Arial;padding:20px;'>"
            + "<h2 style='color:#2E86C1;'>New Job Opportunity 🚀</h2>"
            + "<p>Hi <b>" + name + "</b>,</p>"
            + "<p>A new job has been posted on <b>NaukariSathi</b>.</p>"
            + "<h3>📌 Job Details</h3>"
            + "<p><b>Company:</b> " + companyName + "</p>"
            + "<p><b>Position:</b> " + jobTitle + "</p>"
            + "<p><b>Category:</b> " + category + "</p>"
            + "<p><b>Salary:</b> " + salary + "</p>"
            + "<h3>🎯 Eligibility</h3>"
            + "<p><b>Min CGPA:</b> " + cgpa + "</p>"
            + "<p><b>Year:</b> " + year + "</p>"
            + "<p><b>Branch:</b> " + branch + "</p>"
            + "<p><b>Skills:</b> " + skills + "</p>"
            + "<h3>📝 Description</h3>"
            + "<p>" + description + "</p>"
            + "<small>Placement Cell - RGIT</small>"
            + "</div>";
        sendEmail(toEmail, name, "🚀 New Job at " + companyName + " - " + jobTitle, html);
    }

    @Async
    public void sendAccountDeletionMail(String toEmail, String name, String staffId) {
        String html =
            "<div style='font-family:Arial;padding:20px;'>"
            + "<h2>Account Removal Notification</h2>"
            + "<p>Hi <b>" + name + "</b>,</p>"
            + "<p>Your account has been removed by admin <b>" + staffId + "</b>.</p>"
            + "<p>If this was a mistake, contact the placement office.</p>"
            + "<small>Placement Cell - RGIT</small>"
            + "</div>";
        sendEmail(toEmail, name, "Account Removed - NaukariSathi", html);
    }

    @Async
    public void sendApplicationAcceptedMail(String email, String name) {
        String html =
            "<h2>Congratulations " + name + " 🎉</h2>"
            + "<p>Your application has been <b>ACCEPTED</b>.</p>"
            + "<p>The company will contact you soon.</p>";
        sendEmail(email, name, "Congratulations 🎉 Job Application Accepted", html);
    }

    @Async
    public void sendApplicationRejectedMail(String email, String name) {
        String html =
            "<h3>Hello " + name + "</h3>"
            + "<p>We regret to inform you that your application was not selected.</p>"
            + "<p>Don't worry! Keep applying for other opportunities.</p>";
        sendEmail(email, name, "Job Application Update", html);
    }
}
