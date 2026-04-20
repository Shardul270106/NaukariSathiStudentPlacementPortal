package com.spp.demo.repositories;


import com.spp.demo.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {

    Admin findByStaffId(String staffId);

    Admin findByEmail(String email);

}