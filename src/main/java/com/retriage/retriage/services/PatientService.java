package com.retriage.retriage.services;

import com.retriage.retriage.models.Patient;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    Patient savePatient(Patient patient);

    List<Patient> getAllPatients();

    Optional<Patient> getPatientById(Long id);

    void deletePatient(Long id);
}
