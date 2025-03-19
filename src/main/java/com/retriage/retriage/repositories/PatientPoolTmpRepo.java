package com.retriage.retriage.repositories;

import com.retriage.retriage.models.PatientPoolTmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface PatientPoolTmpRepo extends JpaRepository<PatientPoolTmp, Long> {
    //This is empty, intentionally

}
