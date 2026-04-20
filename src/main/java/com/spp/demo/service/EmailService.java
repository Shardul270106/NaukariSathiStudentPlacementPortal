package com.spp.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOtpMail(String toEmail, String name, String otp) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("OTP Verification - NaukariSathi -Student Placement Portal 🔐");
            helper.setFrom("placements@yourcollege.edu");

            String htmlContent =
                    "<div style='font-family:Arial;text-align:center;padding:20px;'>"
                            + "<h2>Email Verification Required 🔐</h2>"
                            + "<p>Hi <b>" + name + "</b>,</p>"
                            + "<p>Your OTP for email verification is:</p>"
                            + "<h1 style='color:#2E86C1;font-size:36px;'>"
                            + otp +
                            "</h1>"
                            + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
                            + "<br>"
                            + "<small>If you did not register, please ignore this email.</small>"
                            + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async // non-blocking email
    public void sendRegistrationMail(String toEmail, String name) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to NaukariSathi -Student Placement Portal 🎓");
            helper.setFrom("placements@yourcollege.edu");

            String htmlContent =
                    "<div style='font-family:Arial; text-align:center; padding:20px;'>"
                            + "<img src='cid:campusLogo' width='120'/><br><br>"
                            + "<h2>Welcome to the NaukariSathi -Student Placement Portal 🚀</h2>"
                            + "<p>Hi <b>" + name + "</b>,</p>"
                            + "<p>Your registration was successful.</p>"
                            + "<p>You can now:</p>"
                            + "<ul style='list-style:none;'>"
                            + "<li>✔ Apply for campus drives</li>"
                            + "<li>✔ View company postings</li>"
                            + "<li>✔ Track application status</li>"
                            + "</ul>"
                            + "<br>"
                            + "<a href='http://localhost:8080/login' "
                            + "style='background:#2E86C1;color:white;"
                            + "padding:12px 25px;text-decoration:none;"
                            + "border-radius:6px;'>Login to Portal</a>"
                            + "<br><br>"
                            + "<small>Placement Cell<br/>Your College Name</small>"
                            + "</div>";

            helper.setText(htmlContent, true);

            // Inline logo
            // Inline logo
            ClassPathResource logo = new ClassPathResource("static/logo1.png");
            helper.addInline("campusLogo", logo);


            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Async
    public void sendNewJobNotification(
            String toEmail,
            String name,
            String companyName,
            String jobTitle,
            String category,
            String salary,
            Double cgpa,
            String year,
            String branch,
            String skills,
            String description,
            String logoFile
    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🚀 New Job at " + companyName + " - " + jobTitle);
            helper.setFrom("placements@yourcollege.edu");

            String htmlContent =
                    "<div style='font-family:Arial;background:#f4f6f8;padding:20px;'>"

                            + "<div style='max-width:600px;margin:auto;background:white;"
                            + "padding:25px;border-radius:8px;'>"

                            + "<div style='text-align:center;'>"
                            + "<img src='logo1.png' width='120'><br><br>"
                            + "</div>"

                            + "<h2 style='color:#2E86C1;text-align:center;'>New Job Opportunity 🚀</h2>"

                            + "<p>Hi <b>" + name + "</b>,</p>"
                            + "<p>A new job has been posted on the <b>NaukariSathi -Student Placement Portal</b>.</p>"

                            + "<hr>"

                            + "<h3>📌 Job Details</h3>"
                            + "<p><b>Company:</b> " + companyName + "</p>"
                            + "<p><b>Position:</b> " + jobTitle + "</p>"
                            + "<p><b>Category:</b> " + category + "</p>"
                            + "<p><b>Salary:</b> " + salary + "</p>"

                            + "<hr>"

                            + "<h3>🎯 Eligibility</h3>"
                            + "<p><b>Minimum CGPA:</b> " + cgpa + "</p>"
                            + "<p><b>Year:</b> " + year + "</p>"
                            + "<p><b>Branch:</b> " + branch + "</p>"
                            + "<p><b>Skills:</b> " + skills + "</p>"

                            + "<hr>"

                            + "<h3>📝 Job Description</h3>"
                            + "<p>" + description + "</p>"

                            + "<br>"

                            + "<div style='text-align:center;'>"
                            + "<a href='http://localhost:5500/Jobs.html' "
                            + "style='background:#2E86C1;color:white;"
                            + "padding:12px 25px;text-decoration:none;"
                            + "border-radius:6px;font-weight:bold;'>"
                            + "View & Apply"
                            + "</a>"
                            + "</div>"

                            + "<br><br>"
                            + "<small>Placement Cell<br>RGIT</small>"

                            + "</div>"
                            + "</div>";

            helper.setText(htmlContent, true);

            // Load logo from uploads folder
            if (logoFile != null && logoFile.startsWith("http")) {
                try {
                    org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
                    byte[] imageBytes = rt.getForObject(logoFile, byte[].class);
                    helper.addInline("companyLogo",
                            new org.springframework.core.io.ByteArrayResource(imageBytes),
                            "image/png");
                } catch (Exception ignored) {}
            }
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendAccountDeletionMail(String toEmail, String name, String staffId) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Account Removed - NaukariSathi -Student Placement Portal");
            helper.setFrom("placements@yourcollege.edu");

            String htmlContent =
                    "<div style='font-family:Arial;padding:20px;'>"
                            + "<h2>Account Removal Notification</h2>"
                            + "<p>Hi <b>" + name + "</b>,</p>"
                            + "<p>Your account on the <b>Campus Placement Portal</b> "
                            + "has been removed by admin <b>" + staffId + "</b>.</p>"
                            + "<p>If you believe this was done by mistake, "
                            + "please contact the placement office.</p>"
                            + "<br>"
                            + "<small>Placement Cell<br>RGIT</small>"
                            + "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendApplicationAcceptedMail(String email,String name){

        try{

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message,true);

            helper.setTo(email);
            helper.setSubject("Congratulations 🎉 Job Application Accepted");
            helper.setFrom("placements@yourcollege.edu");

            String html =
                    "<h2>Congratulations "+name+" 🎉</h2>"+
                            "<p>Your application has been <b>ACCEPTED</b>.</p>"+
                            "<p>The company will contact you soon.</p>";

            helper.setText(html,true);

            mailSender.send(message);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Async
    public void sendApplicationRejectedMail(String email,String name){

        try{

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message,true);

            helper.setTo(email);
            helper.setSubject("Job Application Update");

            String html =
                    "<h3>Hello "+name+"</h3>"+
                            "<p>We regret to inform you that your application was not selected.</p>"+
                            "<p>Don't worry! Keep applying for other opportunities.</p>";

            helper.setText(html,true);

            mailSender.send(message);

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
