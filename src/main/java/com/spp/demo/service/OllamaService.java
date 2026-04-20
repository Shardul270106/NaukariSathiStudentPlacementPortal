package com.spp.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    // Kept same class name so ResumeParserService needs ZERO changes
    @Autowired
    private GeminiService geminiService;

    public String parseResume(String resumeText) {
        if (resumeText.length() > 8000) {
            resumeText = resumeText.substring(0, 8000);
        }

        String prompt = """
You are an AI resume parser.

First determine if the given document is a RESUME or CV.

A valid resume usually contains:
- personal details (name may appear at the very top without any label)
- education
- skills
- projects or experience

IMPORTANT: In modern resume templates (like Canva), the person's name often appears
at the very TOP of the document as a large heading — with NO label like "Name:".
It is usually the FIRST line or first prominent text. Extract it as fullName.

If the document is NOT a resume, return:
{ "isResume": false, "reason": "Document is not a resume" }

If it IS a resume, return:
{
  "isResume": true,
  "fullName": "",
  "address": "",
  "collegeName": "",
  "branch": "",
  "year": "",
  "semester": "",
  "cgpa": "",
  "preferredField": "",
  "summary": "",
  "skills": [],
  "education": [],
  "experience": [],
  "projects": []
}

Rules:
- Return ONLY valid JSON, no explanation, no markdown, no code fences
- fullName must be the candidate's real full name (first + last), not a section heading
- If a field is missing return ""
- skills, education, experience, projects must be arrays

Resume Text:
""" + resumeText;

        return geminiService.generate(prompt);
    }
}