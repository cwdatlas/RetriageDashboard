package com.retriage.retriage.services;

import com.retriage.retriage.enums.Role;
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
        logger.info("Entering PatientServiceImp constructor with patientRepository: {}", patientRepository);
        this.patientRepository = patientRepository;
        logger.info("Exiting PatientServiceImp constructor");
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
        logger.info("Entering savePatient with patient: {}", patient);
        logger.debug("savePatient - Validating patient: {}", patient);
        try {
            validatePatient(patient);
            logger.debug("savePatient - Patient validation successful.");
            logger.info("savePatient - Saving patient: {}", patient);
            Patient savedPatient = patientRepository.save(patient);
            logger.info("savePatient - Patient saved successfully with ID: {}", savedPatient.getId());
            logger.debug("savePatient - Saved patient details: {}", savedPatient);
            logger.info("Exiting savePatient, returning: {}", savedPatient);
            return savedPatient;
        } catch (IllegalArgumentException e) {
            logger.warn("savePatient - Patient validation failed: {}", e.getMessage());
            logger.info("Exiting savePatient, returning: null");
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
        logger.info("Entering getAllPatients");
        logger.info("getAllPatients - Retrieving all patients...");
        List<Patient> patients = patientRepository.findAll();
        logger.info("getAllPatients - Retrieved {} patients successfully.", patients.size());
        logger.debug("getAllPatients - Retrieved patient list: {}", patients);
        logger.info("Exiting getAllPatients, returning list of size: {}", patients.size());
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
        logger.info("Entering getPatientById with id: {}", id);
        logger.info("getPatientById - Retrieving patient by ID: {}", id);
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            logger.info("getPatientById - Patient found with ID: {}", id);
            logger.debug("getPatientById - Found patient details: {}", patient.get());
        } else {
            logger.info("getPatientById - Patient not found with ID: {}", id);
        }
        logger.info("Exiting getPatientById, returning Optional with value present: {}", patient.isPresent());
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
        logger.info("Entering deletePatient with id: {}", id);
        logger.info("deletePatient - Deleting patient by ID: {}", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("deletePatient - Attempt to delete non-existent patient with ID: {}", id);
            throw new IllegalArgumentException("Patient with ID " + id + " does not exist.");
        }
        logger.info("deletePatient - Patient with ID: {} exists. Proceeding with deletion.", id);
        patientRepository.deleteById(id);
        logger.info("deletePatient - Patient deleted successfully with ID: {}", id);
        logger.info("Exiting deletePatient");
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
        logger.info("Entering updatePatient with id: {} and patient: {}", id, patient);
        logger.info("updatePatient - Checking if patient with ID: {} exists for update.", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("updatePatient - Patient with id {} not found for update.", id);
            logger.info("Exiting updatePatient, returning: false");
            return false; // Return false when patient not found, instead of null/exception
        }
        logger.info("updatePatient - Patient with ID: {} exists. Proceeding with update.", id);
        logger.debug("updatePatient - Validating updated Patient data for ID: {}", id);
        try {
            validatePatient(patient);
            logger.debug("updatePatient - Patient data valid for update.");
            logger.info("updatePatient - Saving updated patient data for ID: {}", id);
            patient.setId(id); // Ensure ID stays the same
            patientRepository.save(patient);
            logger.info("updatePatient - Patient updated successfully with ID: {}", id);
            logger.debug("updatePatient - Updated patient details: {}", patient);
            logger.info("Exiting updatePatient, returning: true");
            return true;
        } catch (IllegalArgumentException e) {
            logger.warn("updatePatient - Patient validation failed: {}", e.getMessage());
            logger.info("Exiting updatePatient, returning: false");
            return false;
        }
    }

    /**
     * validatePatient
     * Validates a Patient object.
     */
    private void validatePatient(Patient patient) {
        logger.debug("Entering validatePatient with patient: {}", patient);
        if (patient == null) {
            logger.warn("validatePatient - Patient object is null.");
            throw new IllegalArgumentException("Patient object cannot be null.");
        }
        if (patient.getCardId() == null || patient.getCardId().trim().isEmpty()) {
            logger.warn("validatePatient - Patient card ID is null or empty.");
            throw new IllegalArgumentException("Patient card ID cannot be null or empty.");
        }
        if (patient.getCondition() == null) {
            logger.warn("validatePatient - Patient condition is null.");
            throw new IllegalArgumentException("Patient condition cannot be null.");
        }
        logger.info("Patient validation successful.");
    }
}