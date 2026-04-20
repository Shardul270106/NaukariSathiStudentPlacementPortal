package com.spp.demo.controllers;

import com.spp.demo.models.PlacementRecord;
import com.spp.demo.service.PlacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/placements")
@CrossOrigin
public class PlacementController {

    @Autowired
    PlacementService service;

    @PostMapping("/add")
    public PlacementRecord addPlacement(@RequestBody PlacementRecord record){
        return service.saveRecord(record);
    }

    @GetMapping("/all")
    public List<PlacementRecord> getAll(){
        return service.getAllRecords();
    }

    @GetMapping("/student/{userId}")
    public List<PlacementRecord> getStudentPlacements(@PathVariable Integer userId){
        return service.getByUser(userId);
    }
}