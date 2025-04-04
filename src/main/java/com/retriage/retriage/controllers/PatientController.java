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
            ErrorResponse errorResponse = new ErrorResponse(errors, HttpStatus.BAD_REQUEST.value(), "INVALID_INPUT");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Patient patient = new Patient();
        patient.setId(patientForm.getId());
        patient.setCardId(patientForm.getCardId());
        patient.setCondition(patientForm.getCondition());
        Patient saved = patientService.savePatient(patient);
        return ResponseEntity
                .created(URI.create("/patients/" + saved.getId()))
                .body(saved);
    }

    /**
     * getAllPatients
     * Returns every previously created Patient object
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    /**
     * getPatientById
     * Returns a Patient given their provided ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        logger.info("Entering getPatientById with id: {}", id);
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        ResponseEntity<Patient> response = optionalPatient
                .map(patient -> {
                    logger.info("getPatientById - Found patient with id: {}", id);
                    return ResponseEntity.ok(patient);
                })
                .orElseGet(() -> {
                    logger.warn("getPatientById - Patient with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
        logger.info("Exiting getPatientById, returning response: {}", response.getStatusCode());
        return response;
    }

    /**
     * deletePatient
     * Deletes a Patient, specified by their ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // This catch block is here temporarily to demonstrate where you MIGHT have handled exceptions.
            // In a real application without try/catch in the controller, you'd handle this globally.
            ErrorResponse errorResponse = new ErrorResponse(List.of("Error deleting patient with id " + id + "."), HttpStatus.INTERNAL_SERVER_ERROR.value(), "DELETE_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * updatePatient
     * Updates an existing Patient, specified by their ID
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        boolean updated = patientService.updatePatient(id, patient);
        if (!updated) {
            ErrorResponse errorResponse = new ErrorResponse(List.of("Patient with id " + id + " not found for update."), HttpStatus.NOT_FOUND.value(), "PATIENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
            ErrorResponse errorResponse = new ErrorResponse(List.of("Patient with id " + id + " not found for partial update."), HttpStatus.NOT_FOUND.value(), "PATIENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
                        errors.add("Invalid field provided for update: " + key);
                }
            } catch (IllegalArgumentException e) {
                errors.add("Invalid value for field " + key + ": " + value);
            }
        });

        if (!errors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse(errors, HttpStatus.BAD_REQUEST.value(), "INVALID_INPUT");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Patient updatedPatient = patientService.savePatient(existingPatient);
        return ResponseEntity.ok(updatedPatient);
    }
}