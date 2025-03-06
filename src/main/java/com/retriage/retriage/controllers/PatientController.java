package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.forms.PatientForm;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.PatientService;
import jakarta.validation.Valid;
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
     * 1) Create a new Patient
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientForm patientForm) {
        //Secondary Validation

        //Setting patient values with validated values from the form
        Patient patient = new Patient();
        patient.setCardId(patientForm.getCardId());
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setCondition(patientForm.getCondition());
        patient.setResourceList(patientForm.getResourceList());
        patient.setRetriageNurse(patientForm.getRetriageNurse());
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

    /**
     * 5) Update an existing Patient
     * PUT /patients/{id}
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        boolean updated = patientService.updatePatient(id, patient);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient);
        //PUT is used for full updates, requires all fields, and
        //replaces the entire record with new data
    }


    /**
     * 6) Partially Update a Patient
     * PATCH /patients/{id}
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Patient> partialUpdatePatient(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        if (optionalPatient.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 if patient doesn't exist
        }

        Patient existingPatient = optionalPatient.get();

        // Apply updates dynamically
        updates.forEach((key, value) -> {
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
                    existingPatient.setCondition(Condition.valueOf((String) value));
                    break;
                case "resourceList":
                    existingPatient.setResourceList((List<Resource>) value);
                    break;
                case "retriageNurse":
                    existingPatient.setRetriageNurse((User) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        Patient updatedPatient = patientService.savePatient(existingPatient);
        return ResponseEntity.ok(updatedPatient);
    }
}
