package com.spp.demo.repositories;


import com.spp.demo.models.PlacementRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlacementRecordRepository extends JpaRepository<PlacementRecord, Long> {

    List<PlacementRecord> findByUserId(Integer userId);
}