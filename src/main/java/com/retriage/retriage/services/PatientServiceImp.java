package com.retriage.retriage.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.repositories.PatientRepository;


@Service
public class PatientServiceImp implements PatientService {

    private final PatientRepository patientRepository;

    // Constructor-based injection (best practice)
    public PatientServiceImp(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Create or update a Patient
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Retrieve all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Retrieve a single Patient by ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Optional: delete, update status, etc.
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
