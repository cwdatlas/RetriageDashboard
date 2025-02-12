package com.retriage.retriage.service;
/**
 * @author John Botonakis
 * @version 1.0
 */

import com.retriage.retriage.domain.Patient;
import com.retriage.retriage.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service // Marks this class as a service, which holds business logic, for Spring to manage.
@Slf4j // Provides a logger named 'log' for logging messages.
@Transactional(rollbackOn = Exception.class) // Ensures that any exception thrown in this class will cause the transaction to rollback.
@RequiredArgsConstructor // Generates a constructor for required fields to facilitate dependency injection.

public class PatientServiceImp implements PatientService{
    // The repository that provides CRUD operations for Patient objects.
    private static PatientRepo patientRepo;

    public void PatientRepo(PatientRepo patientRepo) {
        PatientServiceImp.patientRepo = patientRepo;
    }

    /**
     * Returns all patients, sorted by name in alphabetical order.
     *
     * @param page The current page number (for pagination).
     * @param size The number of patients to return per page.
     * @return A page (a subset) of patients sorted by name.
     */
    public Page<Patient> getAllPatients(int page, int size) {
        // Create a pagination request: page number, page size, and sort by the "name" field.
        return patientRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    /**
     * Returns a patient by a specified ID.
     * If the patient doesn't exist, then a RuntimeException is thrown.
     *
     * @param id The ID to search for.
     * @return The Patient object that matches the given ID.
     */
    public Patient getPatientById(String id) {
        // Attempt to find the patient by ID; if not found, throw an exception with a helpful message.
        return patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    /**
     * Create a new Patient object and save it in the repository.
     *
     * @param patient The Patient object to be saved.
     * @return The saved Patient object.
     */
    public static Patient createPatient(Patient patient) {
        // Save the patient to the repository and return the saved instance.
        return patientRepo.save(patient);
    }

    /**
     * Deletes a given Patient object from the repository.
     *
     * @param patient The Patient object to be deleted.
     */
    public void deletePatient(Patient patient) {
        // Remove the patient record from the repository (and thus the database).
        patientRepo.delete(patient);
    }
}
