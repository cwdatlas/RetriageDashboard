package com.retriage.retriage.repositories;

import com.retriage.retriage.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface ResourceTemplateRepository extends JpaRepository<Resource, Long> {
    //This is empty, intentionally

}
