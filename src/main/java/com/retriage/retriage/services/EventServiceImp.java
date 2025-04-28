package com.retriage.retriage.services;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.repositories.EventRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link EventService} interface.
 * Provides the concrete business logic for managing {@link Event} entities,
 * interacting with the database via {@link EventRepo}.
 */
@Service
public class EventServiceImp implements EventService {

    /**
     * Logger for this service implementation.
     */
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImp.class);
    /**
     * Repository for accessing and managing Event entities in the database.
     */
    private final EventRepo eventRepository;

    /**
     * Constructs an instance of {@code EventServiceImp}.
     *
     * @param eventRepository The {@link EventRepo} used for database operations on events.
     */
    public EventServiceImp(EventRepo eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Saves an event (Create/Update) in the database.
     * Performs validation before saving.
     *
     * @param event The event to save.
     * @return true if the event is saved successfully, false otherwise (e.g., validation failure).
     */
    @Override
    public boolean saveEvent(Event event) {
        if (event == null) {
            logger.warn("saveEvent - Event object is null, cannot save.");
            return false;
        }

        try {
            validateEvent(event); // Call the validation method
            Event savedEvent = eventRepository.save(event);
            logger.info("saveEvent - Event saved successfully with ID: {}", savedEvent.getId());
            return true;
        } catch (IllegalArgumentException e) {
            logger.warn("saveEvent - Event validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves an event by its unique ID.
     *
     * @param id The ID of the event to find.
     * @return The found Event or null if not found.
     */
    @Override
    public Event findEventById(Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        Event event = eventOptional.orElse(null);
        if (event != null) {
            logger.info("findEventById - Found event with ID: {}", id);
            return event;
        } else {
            logger.warn("findEventById - No event found with ID: {}", id);
            return null;
        }

    }

    /**
     * Updates an existing event with new data.
     * Performs validation before updating.
     *
     * @param id    The ID of the event to update.
     * @param event The updated event data.
     * @return The updated event or null if the event was not found or validation failed.
     */
    @Override
    public Event updateEvent(long id, Event event) {
        if (!eventRepository.existsById(id)) {
            logger.warn("UpdateEvent - Event with id {} not found for update.", id);
            return null; // Return null if event doesn't exist
        }
        if (event == null) {
            logger.warn("updateEvent - Provided event object is null.");
            return null;
        }


        try {
            validateEvent(event); // Call the validation method for updates as well
            event.setId(id); // Ensure the ID stays the same
            Event updatedEvent = eventRepository.save(event);
            logger.info("UpdateEvent - Event updated successfully with ID: {}", id);
            return updatedEvent;
        } catch (IllegalArgumentException e) {
            logger.warn("updateEvent - Event validation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves all existing events from the database.
     *
     * @return List of all events. Returns an empty list if no events exist.
     */
    @Override
    public List<Event> findAllEvents() {
        List<Event> events = eventRepository.findAll();
        logger.info("findAllEvents - Retrieved {} events.", events.size());
        return events;
    }

    /**
     * Deletes an event by its unique ID.
     * Checks if the event exists before attempting deletion.
     *
     * @param id The ID of the event to delete.
     */
    @Override
    public void deleteEventById(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            logger.info("deleteEventById - Event deleted successfully with ID: {}", id);
        } else {
            logger.warn("deleteEventById - Event with id {} does not exist.", id);
        }
    }

    /**
     * Finds the currently active (Running) event.
     * If multiple events are found with Status.Running, logs an error and returns null.
     *
     * @return The active {@link Event} entity if exactly one is found with {@link Status#Running}, otherwise {@code null}.
     */
    @Override
    @Transactional
    // Ensures the operation is atomic, potentially needed if status check and retrieval need to be consistent
    public Event findActiveEvent() {
        List<Event> events = eventRepository.findByStatus(Status.Running);
        Event returnEvent;
        if (events.isEmpty()) {
            logger.debug("getActiveEvent: Checked for active events, none found.");
            returnEvent = null;
        } else if (events.size() > 1) {
            logger.error("getActiveEvent: More than one running event found!");
            returnEvent = null;
        } else {
            returnEvent = events.getFirst();

        }
        return returnEvent;
    }

    /**
     * Resets a given event to its initial state (Status.Created) and clears patient-specific data.
     * Specifically, it clears the list of patients from all associated pools and resets event timing.
     *
     * @param event The {@link Event} entity to reset.
     * @return The reset {@link Event} entity, or null if the input event is null.
     */
    @Override
    public Event resetEventById(Event event) {
        if (event == null) return null;
        // Clear patients from all pools
        for (PatientPool pool : event.getPools()) {
            pool.setPatients(new ArrayList<>());
        }
        // Reset timing attributes
        event.setStartTime(System.currentTimeMillis());
        event.setRemainingDuration(event.getDuration());
        // Optionally set status back to Created if needed, depending on desired reset state
        // event.setStatus(Status.Created);
        return event;
    }

    /**
     * Validates an Event object before saving or updating.
     * Checks for null/empty name, null/empty pool list, and null status.
     *
     * @param event The Event object to validate.
     * @throws IllegalArgumentException if the event is invalid (e.g., null/empty fields).
     */
    private void validateEvent(Event event) {
        if (event == null) {
            logger.warn("validateEvent - Event object is null.");
            throw new IllegalArgumentException("Event object cannot be null.");
        }
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            logger.warn("validateEvent - Event name is null or empty.");
            throw new IllegalArgumentException("Event name cannot be null or empty.");
        }
        if (event.getPools() == null || event.getPools().isEmpty()) {
            logger.warn("validateEvent - Resources list is null or empty.");
            throw new IllegalArgumentException("Event must have at least one resource.");
        }
        if (event.getStatus() == null) {
            logger.warn("validateEvent - Status is null.");
            throw new IllegalArgumentException("Status cannot be null.");
        }
    }
}