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
            return ResponseEntity.badRequest().body(new ErrorResponse(errors, 400, "INVALID_INPUT"));
        }

        Patient patient = new Patient();
        patient.setId(patientForm.getId());
        patient.setCardId(patientForm.getCardId());
        patient.setCondition(patientForm.getCondition());
        Patient saved = patientService.savePatient(patient);
        return ResponseEntity.created(URI.create("/patients/" + saved.getId())).body(saved);
    }

    /**
     * getAllPatients
     * Returns every previously created Patient object
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /**
     * getPatientById
     * Returns a Patient given their provided ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        logger.debug("getPatientById - Fetching patient with id: {}", id);
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("getPatientById - Patient with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * deletePatient
     * Deletes a Patient, specified by their ID
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (!patientService.getPatientById(id).isPresent()) {
            logger.warn("deletePatient - Patient with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(List.of("Patient not found"), 404, "DELETE_FAILED"));
        }
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * updatePatient
     * Updates an existing Patient, specified by their ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        if (!patientService.updatePatient(id, patient)) {
            logger.warn("updatePatient - Patient with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(List.of("Patient not found for update."), 404, "PATIENT_ID_NOT_FOUND"));
        }
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
            logger.warn("partialUpdatePatient - Patient with id {} not found", id);
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
            return ResponseEntity.badRequest().body(new ErrorResponse(errors, 400, "INVALID_INPUT"));
        }

        return ResponseEntity.ok(patientService.savePatient(existingPatient));
    }
}