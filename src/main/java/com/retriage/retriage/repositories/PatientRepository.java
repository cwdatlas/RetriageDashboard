package com.retriage.retriage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.retriage.retriage.models.Patient;

/**
 *
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // This interface can remain empty
    // Spring Data JPA automatically provides the basic CRUD methods
}
