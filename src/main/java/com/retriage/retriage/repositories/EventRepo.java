package com.retriage.retriage.repositories;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    // Find all events that are running.
//    @Query("select distinct e from Event e " +
//            "join fetch e.director d " +
//            "left join fetch d.createdPatients " +
//            "where e.status = :status")
    List<Event> findByStatus(Status status);

}
