package com.retriage.retriage.services;

import com.retriage.retriage.models.Patient;
import com.retriage.retriage.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PatientServiceImp implements PatientService {

    // Add Logger for keeping track of any errors
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
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
        logger.info("Validating Patient...");
        validatePatient(patient);
        logger.info("Patient valid. Saving patient {}", patient);
        Patient savedPatient = patientRepository.save(patient);
        logger.info("Patient saved successfully.");
        return savedPatient;
    }

    /**
     * Retrieve all patients
     *
     * @return list of all patients
     */
    @Override
    public List<Patient> getAllPatients() {
        logger.info("Retrieving all patients...");
        List<Patient> patients = patientRepository.findAll();
        logger.info("Retrieved all patients successfully.");
        return patients;
    }

    /**
     * Retrieve a single Patient by ID
     *
     * @param id patient ID
     * @return patient object (if found)
     */
    @Override
    public Optional<Patient> getPatientById(Long id) {
        logger.info("Retrieving patient by ID: {}", id);
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            logger.info("Patient found with ID: {}", id);
        } else {
            logger.info("Patient not found with ID: {}", id);
        }
        return patient;
    }

    /**
     * Deletes a patient by ID
     *
     * @param id patient ID
     */
    @Override
    public void deletePatient(Long id) {
        logger.info("Deleting patient by ID: {}", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent patient with ID: {}", id);
            throw new IllegalArgumentException("Patient with ID " + id + " does not exist.");
        }
        logger.info("Patient with ID: {} exists. Proceeding with deletion.", id);
        patientRepository.deleteById(id);
        logger.info("Patient deleted successfully with ID: {}", id);
    }

    /**
     * Updates a patient if they exist
     *
     * @param patient updated patient object
     * @return true if update is successful, false otherwise
     */
    @Override
    public boolean updatePatient(Long id, Patient patient) {
        logger.info("Updating patient with ID: {}", id);
        logger.info("Checking if patient with ID: {} exists for update.", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("Attempt to update non-existent patient with ID: {}", id);
            throw new IllegalArgumentException("Cannot update: Patient with ID " + id + " does not exist.");
        }
        logger.info("Patient with ID: {} exists. Proceeding with update.", id);
        logger.info("Validating updated Patient data for ID: {}", id);
        validatePatient(patient);
        logger.info("Patient data valid for update. Saving updated patient data for ID: {}", id);
        patient.setId(id); // Ensure ID stays the same
        patientRepository.save(patient);
        logger.info("Patient updated successfully with ID: {}", id);
        return true;
    }

    /**
     * Validates a Patient object.
     */
    private void validatePatient(Patient patient) {
        if (patient == null) {
            logger.warn("Patient object is null.");
            throw new IllegalArgumentException("Patient object cannot be null.");
        }
        if (patient.getCardId() == null || patient.getCardId().trim().isEmpty()) {
            logger.warn("Patient card ID is null or empty.");
            throw new IllegalArgumentException("Patient card ID cannot be null or empty.");
        }
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            logger.warn("Patient first name is null or empty.");
            throw new IllegalArgumentException("Patient first name cannot be null or empty.");
        }
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            logger.warn("Patient last name is null or empty.");
            throw new IllegalArgumentException("Patient last name cannot be null or empty.");
        }
        if (patient.getCondition() == null) {
            logger.warn("Patient condition is null.");
            throw new IllegalArgumentException("Patient condition cannot be null.");
        }
        if (patient.getRetriageNurse() == null) {
            logger.warn("Patient retriage nurse is null.");
            throw new IllegalArgumentException("Each patient must be assigned a retriage nurse.");
        }
    }
}