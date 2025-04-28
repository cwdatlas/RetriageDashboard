package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;

import java.util.List;

/**
 * Service interface defining the contract for managing {@link Event} entities.
 * Implementations of this interface handle the business logic related to events,
 * including creation, retrieval, updating, deletion, and managing the active event state.
 */
public interface EventService {

    /**
     * Saves a given {@link Event} entity.
     *
     * @param event The {@link Event} to save.
     * @return {@code true} if the event was saved successfully, {@code false} otherwise.
     */
    boolean saveEvent(Event event);

    /**
     * Finds an {@link Event} entity by its unique identifier.
     *
     * @param id The unique ID of the event to find.
     * @return The {@link Event} entity if found, otherwise {@code null}.
     */
    Event findEventById(Long id);

    /**
     * Updates an existing {@link Event} entity with new data.
     *
     * @param id    The ID of the event to update.
     * @param event The {@link Event} object containing the updated data.
     * @return The updated {@link Event} entity.
     */
    Event updateEvent(long id, Event event);

    /**
     * Retrieves all existing {@link Event} entities.
     *
     * @return A {@link List} of all {@link Event} entities. Returns an empty list if no events exist.
     */
    List<Event> findAllEvents();

    /**
     * Deletes an {@link Event} entity by its unique identifier.
     *
     * @param id The unique ID of the event to delete.
     */
    void deleteEventById(Long id);

    /**
     * Finds the currently active {@link Event}.
     * The criteria for an "active" event are determined by the service implementation.
     *
     * @return The active {@link Event} entity if one exists, otherwise {@code null}.
     */
    Event findActiveEvent();

    /**
     * Resets the state of a given {@link Event} entity.
     * This typically involves setting its status back to {@code Created} and potentially clearing related progress data.
     *
     * @param event The {@link Event} entity to reset.
     * @return The reset {@link Event} entity.
     */
    Event resetEventById(Event event);
}