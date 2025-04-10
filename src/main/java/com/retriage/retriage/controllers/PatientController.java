package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.forms.PatientForm;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.services.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/patients")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    /**
     * Constructor injection of the service
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * createPatient
     * Creates a new Patient object from the data provided in the PatientForm
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyRole('Director', 'Nurse')") // Restricts to Director and Nurse roles only
    public ResponseEntity<?> createPatient(@Valid @RequestBody PatientForm patientForm) {
        List<String> errors = new ArrayList<>();
        //Secondary Validation
        if (patientForm.getCardId() == null || patientForm.getCardId().trim().isEmpty()) {
            errors.add("Patient card ID cannot be empty.");
        }
        if (patientForm.getCondition() == null) {
            errors.add("Patient condition must be specified.");
        }

        if (!errors.isEmpty()) {
            logger.warn("createPatient - Patient creation failed: Invalid input.");
            return ResponseEntity.badRequest().body(new ErrorResponse(errors, 400, "INVALID_INPUT"));
        }

        Patient patient = new Patient();
        patient.setId(patientForm.getId());
        patient.setCardId(patientForm.getCardId());
        patient.setCondition(patientForm.getCondition());
        Patient saved = patientService.savePatient(patient);
        logger.info("createPatient - Patient created successfully with ID: {}", saved.getId());
        return ResponseEntity.created(URI.create("/patients/" + saved.getId())).body(saved);
    }

    /**
     * getAllPatients
     * Returns every previously created Patient object
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        logger.info("getAllPatients - Retrieved {} patients.", patients.size());
        return ResponseEntity.ok(patients);
    }

    /**
     * getPatientById
     * Returns a Patient given their provided ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            logger.info("getPatientById - Patient found with ID: {}", id);
            return ResponseEntity.ok(patient.get());
        } else {
            logger.warn("getPatientById - Patient find failed: Patient with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * deletePatient
     * Deletes a Patient, specified by their ID
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('Director', 'Nurse')") // Restricts to Director and Nurse roles only
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (!patientService.getPatientById(id).isPresent()) {
            logger.warn("deletePatient - Patient delete failed: Patient with id {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(List.of("Patient not found"), 404, "DELETE_FAILED"));
        }
        patientService.deletePatient(id);
        logger.info("deletePatient - Patient deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * updatePatient
     * Updates an existing Patient, specified by their ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        if (!patientService.updatePatient(id, patient)) {
            logger.warn("updatePatient - Patient update failed: Patient with id {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(List.of("Patient not found for update."), 404, "PATIENT_ID_NOT_FOUND"));
        }
        logger.info("updatePatient - Patient updated with ID: {}", id);
        return ResponseEntity.ok(patient);
    }

    /**
     * partialUpdatePatient
     * Update specific portions of a Patient, specified by their Patient ID
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> partialUpdatePatient(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        if (optionalPatient.isEmpty()) {
            logger.warn("partialUpdatePatient - Patient partial update failed: Patient with id {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(List.of("Patient not found for partial update."), 404, "PATIENT_ID_NOT_FOUND"));
        }

        Patient existingPatient = optionalPatient.get();
        List<String> errors = new ArrayList<>();

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "cardId":
                        existingPatient.setCardId((String) value);
                        break;
                    case "condition":
                        existingPatient.setCondition(Condition.valueOf((String) value));
                        break;
                    default:
                        errors.add("Invalid field provided: " + key);
                }
            } catch (IllegalArgumentException e) {
                errors.add("Invalid value for " + key + ": " + value);
            }
        });

        if (!errors.isEmpty()) {
            logger.warn("partialUpdatePatient - Patient partial update failed: Invalid input.");
            return ResponseEntity.badRequest().body(new ErrorResponse(errors, 400, "INVALID_INPUT"));
        }

        Patient updatedPatient = patientService.savePatient(existingPatient);
        logger.info("partialUpdatePatient - Patient partially updated with ID: {}", updatedPatient.getId());
        return ResponseEntity.ok(updatedPatient);
    }
}