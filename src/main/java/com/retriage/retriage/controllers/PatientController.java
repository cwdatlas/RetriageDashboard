package com.retriage.retriage.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retriage.retriage.models.Patient;
import com.retriage.retriage.services.PatientService;

@RestController
@CrossOrigin
@RequestMapping("/patients")
public class PatientController {
    /**
     *
     */
    private final PatientService patientService;
    /**
     * Constructor injection of the service
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     *1) Create a new Patient
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient saved = patientService.savePatient(patient);
        // Return 201 Created with Location header to point to the new resource
        return ResponseEntity
                .created(URI.create("/patients/" + saved.getId()))
                .body(saved);
    }

    /**
     * 2) Get all Patients
     * GET /patients
     */
    @GetMapping(produces = "application/json")
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        return optionalPatient
                .map(patient -> ResponseEntity.ok(patient))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    /**
     * 4) Delete a Patient
     * DELETE /patients/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
