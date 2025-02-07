package com.retriage.retriage.service;
/**
 * @author John Botonakis
 * @version 1.0
 *
 */

import com.retriage.retriage.domain.Patient;
import com.retriage.retriage.repo.PatientRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepo patientRepo;


    public Page<Patient> getAllPatients(int page, int size) {
        return PatientRepo.findAll(PageRequest.of(page,size, Sort.by("name")));
    }

    public Patient getPatient(String id) {
        return patientRepo.findById(id).orElseThrow() -> new RuntimeException("Patient not found");
    }

    public createPatient (Patient patient){
        return PatientRepo.save(patient);
    }

    public void deletePatient(Patient patient) {
        //Assign later
    }

    public String uploadPhoto(String id, MultipartFile file){
        Patient patient = getPatient(id);
        String photoURL = null;
        patient.setPhotoURL(photoURL);
        PatientRepo.save(patient);
    }

}
