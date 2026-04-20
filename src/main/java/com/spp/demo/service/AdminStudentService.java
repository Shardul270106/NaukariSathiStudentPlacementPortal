package com.spp.demo.service;

import com.spp.demo.dto.StudentAdminDto;
import com.spp.demo.models.StudentProfile;
import com.spp.demo.models.User;
import com.spp.demo.repositories.StudentProfileRepository;
import com.spp.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminStudentService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentProfileRepository profileRepo;

    @Autowired
    private EmailService emailService;   // ⭐ ADD THIS

    // GET ALL STUDENTS
    public List<StudentAdminDto> getAllStudents(){

        List<User> users = userRepo.findByEnabledTrue();

        List<StudentAdminDto> list = new ArrayList<>();

        for(User user : users){

            StudentAdminDto dto = new StudentAdminDto();

            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setContact(user.getContact());

            Optional<StudentProfile> profileOpt =
                    profileRepo.findByUser(user);

            if(profileOpt.isPresent()){

                StudentProfile profile = profileOpt.get();

                dto.setCollegeName(profile.getCollegeName());
                dto.setBranch(profile.getBranch());
                dto.setYear(profile.getYear());
                dto.setCgpa(profile.getCgpa());
                dto.setResumePath(profile.getResumeFilePath());
            }

            list.add(dto);
        }

        return list;
    }

    // DELETE STUDENT + SEND EMAIL
    // DELETE STUDENT + SEND EMAIL
    public void deleteStudent(int id, String staffId){

        Optional<User> userOpt = userRepo.findById(id);

        if(userOpt.isPresent()){

            User user = userOpt.get();

            // SEND EMAIL
            emailService.sendAccountDeletionMail(
                    user.getEmail(),
                    user.getName(),
                    staffId
            );

            // DELETE USER
            userRepo.deleteById(id);
        }
    }

    public StudentAdminDto getStudentById(int id){

        Optional<User> userOpt = userRepo.findById(id);

        if(userOpt.isPresent()){

            User user = userOpt.get();

            StudentAdminDto dto = new StudentAdminDto();

            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setContact(user.getContact());

            Optional<StudentProfile> profileOpt = profileRepo.findByUser(user);

            if(profileOpt.isPresent()){

                StudentProfile profile = profileOpt.get();

                dto.setCollegeName(profile.getCollegeName());
                dto.setBranch(profile.getBranch());
                dto.setYear(profile.getYear());
                dto.setCgpa(profile.getCgpa());
                dto.setResumePath(profile.getResumeFilePath());
            }

            return dto;
        }

        return null;
    }
    public User getStudentFullDetails(int id){

        Optional<User> userOpt = userRepo.findById(id);

        if(userOpt.isPresent()){
            return userOpt.get();
        }

        return null;
    }
}