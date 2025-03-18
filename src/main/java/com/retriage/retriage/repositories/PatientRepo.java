package com.retriage.retriage.repositories;

import com.retriage.retriage.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface PatientRepo extends JpaRepository<Patient, Long> {
    //This is empty, intentionally

}
