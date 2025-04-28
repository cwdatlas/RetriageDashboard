package com.retriage.retriage.repositories;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Event} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 * <p>
 * This interface interacts with the "events" table in the database.
 */
@Repository
public interface EventRepo extends JpaRepository<Event, Long> {

    /**
     * Finds and returns a list of {@link Event} entities that match the given status.
     * Spring Data JPA automatically generates the query for this method based on its name.
     *
     * @param status The {@link Status} to search for (e.g., {@code Status.Running}, {@code Status.Ended}).
     * @return A {@link List} of {@link Event} objects found with the specified status. Returns an empty list if no events are found.
     */
    List<Event> findByStatus(Status status);
}