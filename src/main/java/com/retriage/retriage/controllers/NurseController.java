package com.retriage.retriage.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retriage.retriage.models.Nurse;
import com.retriage.retriage.services.NurseService;

@RestController
@CrossOrigin
@RequestMapping("/nurses")
public class NurseController {
    /**
     * 
     */
    private final NurseService nurseService;

    /**
     *
     * @param nurseService
     */
    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }


    /**
     *
     * @param nurse
     * @return
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Nurse> createNurse(@RequestBody Nurse nurse) {
        Nurse saved = nurseService.saveNurse(nurse);
        // Return 201 Created with Location header to point to the new resource
        return ResponseEntity
                .created(URI.create("/nurses/" + saved.getId()))
                .body(saved);
    }

    /**
     *
     * @return
     */
    @GetMapping(produces = "application/json")
    public List<Nurse> getAllNurses() {
        return nurseService.findAllNurses();
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Nurse> getNurse(@PathVariable Long id) {
        Optional<Nurse> optionalNurse = nurseService.findNurseById(id);
        return optionalNurse
                .map(nurse -> ResponseEntity.ok(nurse))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNurse(@PathVariable Long id) {
        nurseService.deleteNurse(id);
        return ResponseEntity.noContent().build();
    }
}
