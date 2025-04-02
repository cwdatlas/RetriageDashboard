package com.retriage.retriage.services;

import com.retriage.retriage.models.Patient;
import com.retriage.retriage.repositories.PatientRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PatientServiceImp implements PatientService {

    // Add Logger for keeping track of any errors
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImp.class); // Corrected logger name
    private final PatientRepo patientRepository;

    /**
     * PatientServiceImp
     * Patient Service constructor
     *
     * @param patientRepository Repository declared in PatientServiceImp
     */
    public PatientServiceImp(PatientRepo patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * savePatient
     * Saves a patient
     *
     * @param patient The patient to be saved
     * @return The saved Patient
     */
    @Override
    public Patient savePatient(Patient patient) {
        try {
            validatePatient(patient);
            Patient savedPatient = patientRepository.save(patient);
            logger.info("savePatient - Patient saved successfully with ID: {}", savedPatient.getId());
            return savedPatient;
        } catch (IllegalArgumentException e) {
            logger.warn("savePatient - Patient save failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * getAllPatients
     * Retrieve all patients
     *
     * @return list of all patients
     */
    @Override
    public List<Patient> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        logger.info("getAllPatients - Retrieved {} patients.", patients.size());
        return patients;
    }

    /**
     * getPatientById
     * Retrieve a single Patient by ID
     *
     * @param id patient ID
     * @return patient object (if found)
     */
    @Override
    public Optional<Patient> getPatientById(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            logger.info("getPatientById - Patient found with ID: {}", id);
        } else {
            logger.warn("getPatientById - Patient find failed: No patient found with ID: {}", id);
        }
        return patient;
    }

    /**
     * deletePatient
     * Deletes a patient by ID
     *
     * @param id patient ID
     */
    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            logger.warn("deletePatient - Patient delete failed: Patient with ID {} does not exist.", id);
            throw new IllegalArgumentException("Patient with ID " + id + " does not exist.");
        }
        patientRepository.deleteById(id);
        logger.info("deletePatient - Patient deleted successfully with ID: {}", id);
    }

    /**
     * updatePatient
     * Updates a patient if they exist
     *
     * @param patient updated patient object
     * @return true if update is successful, false otherwise
     */
    @Override
    public boolean updatePatient(Long id, Patient patient) {
        if (!patientRepository.existsById(id)) {
            logger.warn("updatePatient - Patient update failed: Patient with id {} not found.", id);
            return false;
        }
        try {
            validatePatient(patient);
            patient.setId(id);
            patientRepository.save(patient);
            logger.info("updatePatient - Patient updated successfully with ID: {}", id);
            return true;
        } catch (IllegalArgumentException e) {
            logger.warn("updatePatient - Patient update failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * validatePatient
     * Validates a Patient object.
     */
    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient object cannot be null.");
        }
        if (patient.getCardId() == null || patient.getCardId().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient card ID cannot be null or empty.");
        }
        if (patient.getCondition() == null) {
            throw new IllegalArgumentException("Patient condition cannot be null.");
        }
    }
}