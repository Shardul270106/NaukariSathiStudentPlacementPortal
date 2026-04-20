package com.spp.demo.controllers;


import com.spp.demo.dto.StudentAdminDto;
import com.spp.demo.models.User;
import com.spp.demo.service.AdminStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/students")
@CrossOrigin(origins="*")
public class AdminStudentController {

    @Autowired
    private AdminStudentService service;

    // GET ALL STUDENTS
    @GetMapping("/all")
    public List<StudentAdminDto> getAllStudents(){
        return service.getAllStudents();
    }

    // DELETE STUDENT
    @DeleteMapping("/delete/{id}/{staffId}")
    public String deleteStudent(
            @PathVariable int id,
            @PathVariable String staffId){

        service.deleteStudent(id, staffId);

        return "Student deleted and notification email sent";
    }

    @GetMapping("/{id}")
    public User getStudent(@PathVariable int id){
        return service.getStudentFullDetails(id);
    }
}