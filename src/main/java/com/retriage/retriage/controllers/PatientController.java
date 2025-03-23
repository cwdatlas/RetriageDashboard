package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.forms.PatientForm;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.EventServiceImp;
import com.retriage.retriage.services.PatientService;
import com.retriage.retriage.services.PatientServiceImp;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    /**
     * Constructor injection of the service
     */
    public PatientController(PatientService patientService) {
        logger.info("Entering PatientController constructor with patientService: {}", patientService);
        this.patientService = patientService;
        logger.info("Exiting PatientController constructor");
    }

    /**
     * createPatient
     * Creates a new Patient object from the data provided in the PatientForm
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientForm patientForm) {
        logger.info("Entering createPatient with patientForm: {}", patientForm);
        //Secondary Validation

        //Setting patient values with validated values from the form
        Patient patient = new Patient();
        patient.setId(patientForm.getId());
        patient.setCardId(patientForm.getCardId());
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setCondition(patientForm.getCondition());
        patient.setPoolList(patientForm.getPoolList());
        patient.setRetriageNurse(patientForm.getRetriageNurse());
        logger.debug("createPatient - Patient object created from form: {}", patient);
        Patient saved = patientService.savePatient(patient);
        logger.info("createPatient - Patient saved with ID: {}", saved.getId());
        // Return 201 Created with Location header to point to the new resource
        ResponseEntity<Patient> response = ResponseEntity
                .created(URI.create("/patients/" + saved.getId()))
                .body(saved);
        logger.info("Exiting createPatient, returning response: {}", response.getStatusCode());
        return response;
    }

    /**
     * getAllPatients
     * Returns every previously created Patient object
     */
    @GetMapping(produces = "application/json")
    public List<Patient> getAllPatients() {
        logger.info("Entering getAllPatients");
        List<Patient> patients = patientService.getAllPatients();
        logger.info("Exiting getAllPatients, returning {} patients", patients.size());
        return patients;
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
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        logger.info("Entering deletePatient with id: {}", id);
        patientService.deletePatient(id);
        logger.info("Exiting deletePatient, patient with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * updatePatient
     * Updates an existing Patient, specified by their ID
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        logger.info("Entering updatePatient with id: {} and patient: {}", id, patient);
        boolean updated = patientService.updatePatient(id, patient);
        if (!updated) {
            logger.warn("updatePatient - Patient with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("updatePatient - Patient with id {} updated successfully", id);
        return ResponseEntity.ok(patient);
        //PUT is used for full updates, requires all fields, and
        //replaces the entire record with new data
    }


    /**
     * partialUpdatePatient
     * Update specific portions of a Patient, specified by their Patient ID
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> partialUpdatePatient(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("Entering partialUpdatePatient with id: {} and updates: {}", id, updates);
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        if (optionalPatient.isEmpty()) {
            logger.warn("partialUpdatePatient - Patient with id {} not found for partial update", id);
            return ResponseEntity.notFound().build(); // 404 if patient doesn't exist
        }

        Patient existingPatient = optionalPatient.get();
        logger.debug("partialUpdatePatient - Retrieved existing patient: {}", existingPatient);

        // Apply updates dynamically
        updates.forEach((key, value) -> {
            logger.debug("partialUpdatePatient - Applying update for field: {}, value: {}", key, value);
            switch (key) {
                case "cardId":
                    existingPatient.setCardId((String) value);
                    break;
                case "firstName":
                    existingPatient.setFirstName((String) value);
                    break;
                case "lastName":
                    existingPatient.setLastName((String) value);
                    break;
                case "condition":
                    try {
                        existingPatient.setCondition(Condition.valueOf((String) value));
                    } catch (IllegalArgumentException e) {
                        logger.warn("partialUpdatePatient - Invalid condition value: {}", value);
                    }
                    break;
                case "resourceList":
                    existingPatient.setPoolList((List<PatientPool>) value);
                    break;
                case "retriageNurse":
                    existingPatient.setRetriageNurse((User) value);
                    break;
                default:
                    logger.warn("partialUpdatePatient - Invalid field provided for update: {}", key);
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
            logger.debug("partialUpdatePatient - Updated patient: {}", existingPatient);
        });

        Patient updatedPatient = patientService.savePatient(existingPatient);
        logger.info("partialUpdatePatient - Patient with id {} partially updated", id);
        return ResponseEntity.ok(updatedPatient);
    }
}