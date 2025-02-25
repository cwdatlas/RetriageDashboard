package com.retriage.retriage.services;

import com.retriage.retriage.models.Patient;

import java.util.List;
import java.util.Optional;

/**
 *
 */
//TODO add all CRUD functions. Create Read Update and Delete. Some are already here, but more needs to be done
public interface PatientService {

    Patient savePatient(Patient patient);

    List<Patient> getAllPatients();

    Optional<Patient> getPatientById(Long id);

    void deletePatient(Long id);

    boolean updatePatient(Long id, Patient patient);
}
