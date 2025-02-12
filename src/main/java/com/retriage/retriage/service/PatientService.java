package com.retriage.retriage.service;

import com.retriage.retriage.domain.Patient;
import org.springframework.data.domain.Page;

public interface PatientService {

    static Patient createPatient(Patient patient) {
        return patient;
    }

    Page<Patient> getAllPatients(int page, int size);

    Patient getPatientById(String id);
}
