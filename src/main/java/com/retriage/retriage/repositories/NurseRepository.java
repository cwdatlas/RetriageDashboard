package com.retriage.retriage.repositories;

import com.retriage.retriage.models.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface NurseRepository extends JpaRepository<Nurse,Long> {
    //Supposed to be empty
}
