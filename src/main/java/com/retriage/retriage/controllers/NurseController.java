package com.retriage.retriage.controllers;


import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retriage.retriage.models.RTNurse;
import com.retriage.retriage.services.NurseService;

@RestController
@CrossOrigin
@RequestMapping("/nurses")
public class NurseController {
    private final NurseService nurseService;

    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<RTNurse> createPatient(@RequestBody RTNurse rtNurse) {
        RTNurse saved = NurseService.savePatient(rtNurse);
        // Return 201 Created with Location header to point to the new resource
        return ResponseEntity
                .created(URI.create("/patients/" + saved.getId()))
                .body(saved);
    }
}
