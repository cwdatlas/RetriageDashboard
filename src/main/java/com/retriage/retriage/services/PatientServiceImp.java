package com.retriage.retriage.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.repositories.PatientRepository;


@Service
public class PatientServiceImp implements PatientService {

    private final PatientRepository patientRepository;

    /**
     * Patient Service constructor
     * @param patientRepository Repository declared in PatientServiceImp
     */
    public PatientServiceImp(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Saves a patient
     * @param patient
     * @return
     */
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Retrieve all patients
     * @return
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieve a single Patient by ID
     * @param id
     * @return
     */
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Optional: delete, update status, etc.
     * @param id
     */
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
