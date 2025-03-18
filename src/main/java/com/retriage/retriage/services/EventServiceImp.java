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
        this.eventRepository = eventRepository;
        logger.debug("EventServiceImp constructor: eventRepository instance = {}", eventRepository);
    }

    /**
     * Saves an event (Create/Update)
     *
     * @param event The event to save
     * @return true if the event is saved successfully, false otherwise
     */
    @Override
    public boolean saveEvent(Event event) {
        logger.info("** Starting to save event **");
        logger.debug("saveEvent: Event details - {}", event);

        if (event == null) {
            logger.warn("saveEvent: Event object is null, cannot save.");
            return false;
        }

        validateEvent(event); // Call the validation method
        logger.debug("saveEvent: Event validation passed.");

        Event savedEvent = eventRepository.save(event);
        boolean isSaved = true;
        logger.info("saveEvent: Event saved successfully with ID: {}", savedEvent.getId());
        logger.debug("saveEvent: Saved Event details - {}", savedEvent);
        return isSaved;
    }

    /**
     * Retrieves an event by ID
     *
     * @param id The ID of the event
     * @return The found Event or null if not found
     */
    @Override
    public Event findEventById(Long id) {
        logger.info("** Starting to find event by ID: {} **", id);
        logger.debug("findEventById: About to call eventRepository.findById({})", id);
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {
            logger.debug("findEventById: Event found with ID: {}", id);
            logger.info("findEventById: Event found with ID: {}", id);
            return eventOptional.get();
        } else {
            logger.warn("findEventById: No event found with ID: {}", id);
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
    public Event UpdateEvent(long id, Event event) {
        logger.info("** Starting to update event with ID: {} **", id);
        logger.debug("UpdateEvent: Event details for update - ID: {}, Event: {}", id, event);
        if (!eventRepository.existsById(id)) {
            logger.warn("UpdateEvent: Event with id {} not found for update.", id);
            return null; // Return null if event doesn't exist
        }

        validateEvent(event); // Call the validation method for updates as well
        logger.debug("UpdateEvent: Event validation passed for update.");

        event.setId(id); // Ensure the ID stays the same
        Event updatedEvent = eventRepository.save(event);
        logger.info("UpdateEvent: Event updated successfully with ID: {}", id);
        logger.debug("UpdateEvent: Updated Event details - {}", updatedEvent);
        return updatedEvent;
    }


    /**
     * Retrieves all events
     *
     * @return List of all events
     */
    @Override
    public List<Event> findAllEvents() {
        logger.info("** Starting to retrieve all events **");
        logger.debug("findAllEvents: About to call eventRepository.findAll()");
        List<Event> events = eventRepository.findAll();
        logger.debug("findAllEvents: Retrieved {} events", events.size());
        logger.info("findAllEvents: Successfully retrieved {} events.", events.size());
        return events;
    }

    /**
     * Deletes an event by ID
     *
     * @param id The ID of the event to delete
     */
    @Override
    public void deleteEventById(Long id) {
        logger.info("** Starting to delete event with ID: {} **", id);
        logger.debug("deleteEventById: Checking if event with ID {} exists", id);
        if (eventRepository.existsById(id)) {
            logger.debug("deleteEventById: Event with ID {} exists. Proceeding with deletion.", id);
            eventRepository.deleteById(id);
            logger.info("deleteEventById: Event deleted successfully with ID: {}", id);
        } else {
            logger.warn("deleteEventById: Event with id {} does not exist.", id);
        }
    }

    /**
     * Validates an Event object before saving or updating.
     *
     * @param event The Event object to validate.
     * @throws IllegalArgumentException if the event is invalid.
     */
    private void validateEvent(Event event) {
        logger.debug("** Starting event validation **");
        if (event == null) {
            logger.warn("validateEvent: Event object is null.");
            throw new IllegalArgumentException("Event object cannot be null.");
        }
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            logger.warn("validateEvent: Event name is null or empty.");
            throw new IllegalArgumentException("Event name cannot be null or empty.");
        }
        if (event.getDirector() == null) {
            logger.warn("validateEvent: Director is null.");
            throw new IllegalArgumentException("Director cannot be null.");
        }
        if (event.getNurses() == null || event.getNurses().isEmpty()) {
            logger.warn("validateEvent: Nurses list is null or empty.");
            throw new IllegalArgumentException("Event must have at least one nurse.");
        }
        if (event.getResources() == null || event.getResources().isEmpty()) {
            logger.warn("validateEvent: Resources list is null or empty.");
            throw new IllegalArgumentException("Event must have at least one resource.");
        }
        if (event.getStatus() == null) {
            logger.warn("validateEvent: Status is null.");
            throw new IllegalArgumentException("Status cannot be null.");
        }
        if (event.getStartTime() >= event.getEndTime()) {
            logger.warn("validateEvent: Start time is not before end time.");
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        logger.debug("validateEvent: Event validation passed successfully.");
    }

}
