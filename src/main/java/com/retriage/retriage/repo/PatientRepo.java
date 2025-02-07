package com.retriage.retriage.repo;


import com.retriage.retriage.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<Patient, String> {

    Optional<Patient> findByID(String id);
    Optional <Patient> findByPhone(String phone);
    Optional <Patient> findByFName(String firstname);
    Optional <Patient> findByLName(String lastname);

}
