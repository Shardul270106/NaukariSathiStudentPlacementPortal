package com.spp.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spp.demo.models.Job;
import com.spp.demo.repositories.JobRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private JobRepo jobRepo;

    public String askAI(String question) {

        String q = question.toLowerCase();
        List<Job> jobs = jobRepo.findAll();

        // Fast-path: company list question — no AI needed
        if (q.contains("company") || q.contains("companies") || q.contains("jobs")) {
            List<String> companies = jobs.stream()
                    .map(Job::getCompanyName)
                    .distinct()
                    .collect(Collectors.toList());

            return "Companies currently available in the placement portal are:\n\n"
                    + String.join(", ", companies);
        }

        String jobData = jobs.stream()
                .map(j ->
                        "Company: " + j.getCompanyName() +
                                ", Role: "    + j.getJobTitle() +
                                ", Skills: "  + j.getSkills() +
                                ", Branch: "  + j.getBranch() +
                                ", CGPA: "    + j.getCgpa()
                )
                .collect(Collectors.joining("\n"));

        String prompt =
                "You are an AI assistant for a Student Placement Portal.\n\n" +

                        "Important rules:\n" +
                        "- Only use the companies present in the job list below.\n" +
                        "- Do NOT invent new companies.\n" +
                        "- If question is about placement preparation, roadmap, or interview tips, answer normally.\n" +
                        "- If question is about companies or jobs, refer ONLY to the job list below.\n\n" +

                        "Available Jobs in Portal:\n" + jobData + "\n\n" +

                        "Student Question:\n" + question + "\n\n" +

                        "Answer clearly for a student:";

        return geminiService.generate(prompt);
    }
}