package com.retriage.retriage.repositories;

import com.retriage.retriage.models.PatientPoolTmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link PatientPoolTmp} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 * <p>
 * This interface interacts with the "patient_pool_templates" table in the database
 * and intentionally contains no custom query methods beyond those provided by JpaRepository.
 */
@Repository
public interface PatientPoolTmpRepo extends JpaRepository<PatientPoolTmp, Long> {
    //This is empty, intentionally

}