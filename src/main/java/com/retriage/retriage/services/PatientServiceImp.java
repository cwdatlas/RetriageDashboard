package com.retriage.retriage.services;

import com.retriage.retriage.models.Patient;
import com.retriage.retriage.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PatientServiceImp implements PatientService {

    private final PatientRepository patientRepository;

    /**
     * Patient Service constructor
     *
     * @param patientRepository Repository declared in PatientServiceImp
     */
    public PatientServiceImp(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }


    /**
     * Saves a patient
     *
     * @param patient The patient to be saved
     * @return The saved Patient
     */
    @Override
    public Patient savePatient(Patient patient) {
        validatePatient(patient);
        return patientRepository.save(patient);
    }

    /**
     * Retrieve all patients
     *
     * @return list of all patients
     */
    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieve a single Patient by ID
     *
     * @param id patient ID
     * @return patient object (if found)
     */
    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Deletes a patient by ID
     *
     * @param id patient ID
     */
    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient with ID " + id + " does not exist.");
        }
        patientRepository.deleteById(id);
    }

    /**
     * Updates a patient if they exist
     *
     * @param patient updated patient object
     * @return true if update is successful, false otherwise
     */
    @Override
    public boolean updatePatient(Long id, Patient patient) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot update: Patient with ID " + id + " does not exist.");
        }

        validatePatient(patient);
        patient.setId(id); // Ensure ID stays the same
        patientRepository.save(patient);
        return true;
    }

    /**
     * Validates a Patient object.
     */
    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient object cannot be null.");
        }
        if (patient.getCardId() == null || patient.getCardId().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient card ID cannot be null or empty.");
        }
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient first name cannot be null or empty.");
        }
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient last name cannot be null or empty.");
        }
        if (patient.getCondition() == null) {
            throw new IllegalArgumentException("Patient condition cannot be null.");
        }
        if (patient.getRetriageNurse() == null) {
            throw new IllegalArgumentException("Each patient must be assigned a retriage nurse.");
        }
    }
}