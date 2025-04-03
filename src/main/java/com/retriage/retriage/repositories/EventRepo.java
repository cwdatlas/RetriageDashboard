package com.retriage.retriage.repositories;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findByStatus(Status status);
}
