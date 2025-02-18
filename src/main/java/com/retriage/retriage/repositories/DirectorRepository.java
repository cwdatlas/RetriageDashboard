package com.retriage.retriage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.retriage.retriage.models.Director;

/**
 *
 */
@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    //This is empty, intentionally

}
