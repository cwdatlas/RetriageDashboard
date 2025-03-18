package com.retriage.retriage.repositories;

import com.retriage.retriage.models.PatientPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface PatientPoolTmpRepo extends JpaRepository<PatientPool, Long> {
    //This is empty, intentionally

}
