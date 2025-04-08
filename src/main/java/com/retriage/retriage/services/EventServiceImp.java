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


@Service
public class EventServiceImp implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImp.class);
    private final EventRepo eventRepository;

    public EventServiceImp(EventRepo eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Saves an event (Create/Update)
     *
     * @param event The event to save
     * @return true if the event is saved successfully, false otherwise
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
     * Retrieves an event by ID
     *
     * @param id The ID of the event
     * @return The found Event or null if not found
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
     * Updates an existing event
     *
     * @param id    The ID of the event to update
     * @param event The updated event data
     * @return The updated event or null if not found
     */
    @Override
    public Event updateEvent(long id, Event event) {
        if (!eventRepository.existsById(id)) {
            logger.warn("UpdateEvent - Event with id {} not found for update.", id);
            return null; // Return null if event doesn't exist
        }

        try {
            validateEvent(event); // Call the validation method for updates as well
            event.setId(id); // Ensure the ID stays the same
            Event updatedEvent = eventRepository.save(event);
            logger.info("UpdateEvent - Event updated successfully with ID: {}", id);
            return updatedEvent;
        } catch (IllegalArgumentException e) {
            logger.warn("updateEvent - Event validation failed");
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
        List<Event> events = eventRepository.findAll();
        logger.info("findAllEvents - Retrieved {} events.", events.size());
        return events;
    }

    /**
     * Deletes an event by ID
     *
     * @param id The ID of the event to delete
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

    @Override
    @Transactional
    public Event findActiveEvent() {
        List<Event> events = eventRepository.findByStatus(Status.Running);
        Event returnEvent = new Event();
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

    @Override
    public Event resetEventById(Event event) {
        if (event == null) return null;
        for (PatientPool pool : event.getPools()) {
            pool.setPatients(new ArrayList<>());
        }
        event.setStartTime(System.currentTimeMillis());
        event.setRemainingDuration(event.getDuration());
        return event;
    }

    /**
     * Validates an Event object before saving or updating.
     *
     * @param event The Event object to validate.
     * @throws IllegalArgumentException if the event is invalid.
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