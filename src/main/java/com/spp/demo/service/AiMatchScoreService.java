package com.spp.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiMatchScoreService {

    @Autowired
    private GeminiService geminiService;

    private final ObjectMapper mapper = new ObjectMapper();

    public String calculateMatch(String jobSkills, String studentSkills) {

        String safeJobSkills     = (jobSkills     == null || jobSkills.isBlank())     ? "none" : jobSkills.trim();
        String safeStudentSkills = (studentSkills == null || studentSkills.isBlank()) ? "none" : studentSkills.trim();

        String prompt = """
You are a strict ATS (Applicant Tracking System) skill matcher.

YOUR ONLY JOB:
- Compare the STUDENT SKILLS list against the JOB SKILLS list.
- Decide which JOB SKILLS the student has, and which are missing.

STRICT RULES — FOLLOW EVERY RULE WITHOUT EXCEPTION:
1. You MUST only use skills that appear WORD-FOR-WORD in the lists below.
2. matchedSkills must be a subset of JOB SKILLS only.
3. missingSkills must be a subset of JOB SKILLS only.
4. NEVER invent, guess, infer, or add any skill not literally present in JOB SKILLS.
5. NEVER use skills from your own knowledge or training data.
6. If STUDENT SKILLS is empty or "none", every job skill is missing — score = 0.
7. If JOB SKILLS is empty or "none", both lists are empty — score = 100.
8. score = (number of matched job skills / total job skills) x 100, rounded to nearest integer.
9. Output ONLY the raw JSON object below. No explanation, no markdown, no code fences.

OUTPUT FORMAT (JSON only):
{
  "score": <integer 0-100>,
  "matchedSkills": [<matched job skills only>],
  "missingSkills": [<unmatched job skills only>]
}

JOB SKILLS:
""" + safeJobSkills + """

STUDENT SKILLS:
""" + safeStudentSkills + """

JSON:""";

        try {
            String aiText = geminiService.generate(prompt).trim();

            // Strip markdown code fences if Gemini adds them
            if (aiText.startsWith("```")) {
                aiText = aiText.replaceAll("```[a-z]*\\n?", "").replace("```", "").trim();
            }

            // Extract only the JSON block
            int start = aiText.indexOf("{");
            int end   = aiText.lastIndexOf("}");
            if (start == -1 || end == -1) {
                throw new RuntimeException("No JSON found in response: " + aiText);
            }

            return aiText.substring(start, end + 1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI Match Score Failed: " + e.getMessage());
        }
    }
}