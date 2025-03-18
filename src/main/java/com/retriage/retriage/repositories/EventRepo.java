package com.retriage.retriage.repositories;

import com.retriage.retriage.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    //This is empty, intentionally

}
