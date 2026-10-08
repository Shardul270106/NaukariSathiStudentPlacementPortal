package com.spp.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    // Kept same class name so ResumeParserService needs ZERO changes
    @Autowired
    private GeminiService geminiService;

    public String parseResume(String resumeText) {

        if (resumeText == null || resumeText.isBlank()) {
            throw new RuntimeException("No resume text could be extracted from PDF");
        }

        if (resumeText.length() > 8000) {
            resumeText = resumeText.substring(0, 8000);
        }

        String prompt = """
You are a highly reliable AI resume/CV parser.

Your task is to analyze ONLY the document text provided below.

IMPORTANT:
This text was extracted from a PDF resume using a PDF text extractor.
The formatting, font sizes, columns, colors, images, and visual layout may be lost.
Therefore, DO NOT reject a document simply because the extracted text looks poorly formatted.

==================================================
RESUME DETECTION RULES
==================================================

Consider the document a RESUME/CV if it contains several indicators such as:

- A person's name
- Education / college / university
- Degree / branch / academic qualification
- Skills
- Projects
- Work experience / internship
- Career objective / summary
- Contact information
- CGPA / percentage / graduation year
- Certifications
- Achievements
- Technical skills

A resume does NOT need to contain every field.

Even if only some of these sections are present, it can still be a valid resume.

DO NOT mark the document as non-resume because:
- some fields are missing
- address is missing
- experience is missing
- projects are missing
- CGPA is missing
- semester is missing
- the name has no "Name:" label
- the extracted text has unusual spacing
- the PDF was created using Canva or another design tool
- the resume uses columns
- the extracted text order is imperfect

If the document clearly contains personal/academic/professional information belonging to a candidate, treat it as a RESUME.

Only return isResume=false when the document is clearly NOT a resume, such as:
- a textbook
- an academic question paper
- an invoice
- a receipt
- a random article
- a restaurant menu
- a blank document
- unrelated notes
- a completely unrelated document

==================================================
FULL NAME EXTRACTION
==================================================

The candidate's name may appear:

- as the first line
- as a large heading
- without any label
- before contact information
- inside a Canva-style template

Extract the candidate's actual name into "fullName".

Do NOT use section headings such as:
- RESUME
- CV
- CURRICULUM VITAE
- EDUCATION
- SKILLS
- PROJECTS
- EXPERIENCE
- PROFILE

as the person's name.

If the name cannot be confidently identified, return an empty string.

==================================================
DATA EXTRACTION RULES
==================================================

Extract information ONLY from the provided document.

NEVER invent information.

If a field is not present, return an empty string or empty array.

Preserve the actual information from the document.

Return:
- skills as an array
- education as an array
- experience as an array
- projects as an array

==================================================
OUTPUT FORMAT
==================================================

Return ONLY ONE VALID JSON OBJECT.

Do NOT return:
- markdown
- ```json
- explanations
- comments
- additional text

For a valid resume use EXACTLY this structure:

{
  "isResume": true,
  "reason": "",
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

For a non-resume use:

{
  "isResume": false,
  "reason": "Document is not a resume",
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

==================================================
DOCUMENT TEXT
==================================================

""" + resumeText;

        return geminiService.generate(prompt);
    }
}
