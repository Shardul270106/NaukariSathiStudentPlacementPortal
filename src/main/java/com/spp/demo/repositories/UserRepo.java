package com.spp.demo.repositories;

import com.spp.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {

    User findByUsername(String username);

    User findByCollegeid(String collegeid);

    User findByMuenrollmentid(String muenrollmentid);

    User findByAbcid(String abcid);

    List<User> findByEnabledTrue();


}
