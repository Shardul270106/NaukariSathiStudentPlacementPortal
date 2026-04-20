package com.spp.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spp.demo.models.StudentProfile;
import com.spp.demo.models.User;
import com.spp.demo.repositories.StudentProfileRepository;
import com.spp.demo.repositories.UserRepo;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class ResumeParserService {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentProfileRepository profileRepo;

    @Autowired
    private CloudinaryService cloudinaryService;      // ← ADD THIS

    private final ObjectMapper mapper = new ObjectMapper();

    public StudentProfile parseAndSaveResume(Integer userId, MultipartFile file) {

        try {

            // ===== 1 Extract Resume Text =====
            Tika tika = new Tika();
            String resumeText = tika.parseToString(file.getInputStream());
            if(resumeText.length() > 4000){
                resumeText = resumeText.substring(0, 4000);
            }

            if(!file.getContentType().equals("application/pdf")){
                throw new RuntimeException("Only PDF resumes are allowed");
            }

            // ===== 2 Send Resume to AI =====
            String aiResponse = ollamaService.parseResume(resumeText);

            System.out.println("AI RESPONSE RAW:");
            System.out.println(aiResponse);

            aiResponse = aiResponse.trim();

            if(aiResponse.startsWith("\"")){
                aiResponse = aiResponse.substring(1, aiResponse.length()-1);
            }

            int start = aiResponse.indexOf("{");
            int end = aiResponse.lastIndexOf("}");

            if(start == -1 || end == -1 || end <= start){
                throw new RuntimeException("Invalid AI JSON response");
            }

            String json = aiResponse.substring(start, end + 1);

            System.out.println("CLEAN JSON:");
            System.out.println(json);

            JsonNode node = mapper.readTree(json);
            String resumeName = getSafe(node, "fullName").toLowerCase().trim();

            // ===== Validate if document is resume =====
            if(node.has("isResume") && !node.get("isResume").asBoolean()){
                throw new RuntimeException("Uploaded file is not a valid resume");
            }

            // ===== 3 Find User =====
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String dbName = user.getName().toLowerCase().trim();

            String[] dbParts = dbName.split(" ");
            String firstName = dbParts[0];
            String lastName = dbParts[dbParts.length - 1];

            if(!(resumeName.contains(firstName) && resumeName.contains(lastName))){
                throw new RuntimeException("Resume name does not match registered user name");
            }

            // ===== 4 Check if profile already exists =====
            StudentProfile profile;

            if (user.getStudentProfile() != null) {
                profile = user.getStudentProfile();
            } else {
                profile = new StudentProfile();
                profile.setUser(user);
                profile.setCreatedAt(LocalDateTime.now());
            }

            // ===== 5 Fill AI Extracted Fields =====
            profile.setAddress(getSafe(node, "address"));
            profile.setCollegeName(getSafe(node, "collegeName"));
            profile.setBranch(getSafe(node, "branch"));
            profile.setYear(getSafe(node, "year"));
            profile.setSemester(getSafe(node, "semester"));
            profile.setCgpa(getSafe(node, "cgpa"));
            profile.setPreferredField(getSafe(node, "preferredField"));
            profile.setSummary(getSafe(node, "summary"));
            profile.setSkills(getSafe(node, "skills"));
            profile.setEducation(getSafe(node, "education"));
            profile.setExperience(getSafe(node, "experience"));
            profile.setProjects(getSafe(node, "projects"));

            // ===== 6 Upload PDF to Cloudinary =====  ← ONLY THIS SECTION CHANGED
            String fileUrl = cloudinaryService.uploadPdf(file, userId);
            profile.setResumeFilePath(fileUrl);

            profile.setUpdatedAt(LocalDateTime.now());

            // ===== 7 Save profile =====
            StudentProfile savedProfile = profileRepo.save(profile);

            // ===== 8 Link profile to user =====
            user.setStudentProfile(savedProfile);
            userRepo.save(user);

            return savedProfile;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resume parsing failed: " + e.getMessage());
        }
    }

    private String getSafe(JsonNode node, String field) {
        if(!node.has(field) || node.get(field).isNull()){
            return "";
        }
        JsonNode value = node.get(field);
        if(value.isArray()){
            StringBuilder sb = new StringBuilder();
            for(JsonNode item : value){
                sb.append(item.asText()).append(", ");
            }
            return sb.toString().replaceAll(", $", "");
        }
        return value.asText();
    }
}