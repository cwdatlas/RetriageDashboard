package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;
import com.retriage.retriage.repositories.EventRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class EventServiceImp implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImp.class);
    private final EventRepo eventRepository;

    public EventServiceImp(EventRepo eventRepository) {
        logger.info("Entering EventServiceImp constructor with eventRepository: {}", eventRepository);
        this.eventRepository = eventRepository;
        logger.info("Exiting EventServiceImp constructor");
    }

    /**
     * Saves an event (Create/Update)
     *
     * @param event The event to save
     * @return true if the event is saved successfully, false otherwise
     */
    @Override
    public boolean saveEvent(Event event) {
        logger.info("Entering saveEvent with event: {}", event);

        if (event == null) {
            logger.warn("saveEvent - Event object is null, cannot save.");
            return false;
        }

        logger.debug("saveEvent - Validating event: {}", event);
        try {
            validateEvent(event); // Call the validation method
            logger.debug("saveEvent - Event validation passed.");
            Event savedEvent = eventRepository.save(event);
            logger.info("saveEvent - Event saved successfully with ID: {}", savedEvent.getId());
            logger.debug("saveEvent - Saved Event details: {}", savedEvent);
            logger.info("Exiting saveEvent, returning: true");
            return true;
        } catch (IllegalArgumentException e) {
            logger.warn("saveEvent - Event validation failed: {}", e.getMessage());
            logger.info("Exiting saveEvent, returning: false");
            return false;
        }
    }

    /**
     * Retrieves an event by ID
     *
     * @param id The ID of the event
     * @return The found Event or null if not found
     */
    @Override
    public Event findEventById(Long id) {
        logger.info("Entering findEventById with id: {}", id);
        logger.debug("findEventById - Calling eventRepository.findById({})", id);
        Optional<Event> eventOptional = eventRepository.findById(id);
        Event event = eventOptional.orElse(null);
        if (event != null) {
            logger.info("findEventById - Found event with ID: {}", id);
            logger.debug("findEventById - Retrieved event details: {}", event);
        } else {
            logger.warn("findEventById - No event found with ID: {}", id);
        }
        logger.info("Exiting findEventById, returning: {}", event);
        return event;
    }

    /**
     * Updates an existing event
     *
     * @param id    The ID of the event to update
     * @param event The updated event data
     * @return The updated event or null if not found
     */
    @Override
    public Event UpdateEvent(long id, Event event) {
        logger.info("Entering UpdateEvent with id: {} and event: {}", id, event);
        if (!eventRepository.existsById(id)) {
            logger.warn("UpdateEvent - Event with id {} not found for update.", id);
            logger.info("Exiting UpdateEvent, returning: null");
            return null; // Return null if event doesn't exist
        }

        logger.debug("UpdateEvent - Validating event for update: {}", event);
        try {
            validateEvent(event); // Call the validation method for updates as well
            logger.debug("UpdateEvent - Event validation passed for update.");
            event.setId(id); // Ensure the ID stays the same
            Event updatedEvent = eventRepository.save(event);
            logger.info("UpdateEvent - Event updated successfully with ID: {}", id);
            logger.debug("UpdateEvent - Updated Event details: {}", updatedEvent);
            logger.info("Exiting UpdateEvent, returning: {}", updatedEvent);
            return updatedEvent;
        } catch (IllegalArgumentException e) {
            logger.warn("UpdateEvent - Event validation failed: {}", e.getMessage());
            logger.info("Exiting UpdateEvent, returning: null");
            return null;
        }
    }

    /**
     * Retrieves all events
     *
     * @return List of all events
     */
    @Override
    public List<Event> findAllEvents() {
        logger.info("Entering findAllEvents");
        logger.debug("findAllEvents - Calling eventRepository.findAll()");
        List<Event> events = eventRepository.findAll();
        logger.info("findAllEvents - Retrieved {} events.", events.size());
        logger.debug("findAllEvents - Retrieved event list: {}", events);
        logger.info("Exiting findAllEvents, returning list of size: {}", events.size());
        return events;
    }

    /**
     * Deletes an event by ID
     *
     * @param id The ID of the event to delete
     */
    @Override
    public void deleteEventById(Long id) {
        logger.info("Entering deleteEventById with id: {}", id);
        logger.debug("deleteEventById - Checking if event with ID {} exists", id);
        if (eventRepository.existsById(id)) {
            logger.debug("deleteEventById - Event with ID {} exists. Proceeding with deletion.", id);
            eventRepository.deleteById(id);
            logger.info("deleteEventById - Event deleted successfully with ID: {}", id);
        } else {
            logger.warn("deleteEventById - Event with id {} does not exist.", id);
        }
        logger.info("Exiting deleteEventById");
    }

    /**
     * Validates an Event object before saving or updating.
     *
     * @param event The Event object to validate.
     * @throws IllegalArgumentException if the event is invalid.
     */
    private void validateEvent(Event event) {
        logger.debug("Entering validateEvent with event: {}", event);
        if (event == null) {
            logger.warn("validateEvent - Event object is null.");
            throw new IllegalArgumentException("Event object cannot be null.");
        }
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            logger.warn("validateEvent - Event name is null or empty.");
            throw new IllegalArgumentException("Event name cannot be null or empty.");
        }
        if (event.getDirector() == null) {
            logger.warn("validateEvent - Director is null.");
            throw new IllegalArgumentException("Director cannot be null.");
        }
        if (event.getNurses() == null) {
            logger.warn("validateEvent - Nurses list is null");
            throw new IllegalArgumentException("Event must be initialized");
        }
        if (event.getPools() == null || event.getPools().isEmpty()) {
            logger.warn("validateEvent - Resources list is null or empty.");
            throw new IllegalArgumentException("Event must have at least one resource.");
        }
        if (event.getStatus() == null) {
            logger.warn("validateEvent - Status is null.");
            throw new IllegalArgumentException("Status cannot be null.");
        }
        logger.debug("Exiting validateEvent - Event validation passed.");
    }
}