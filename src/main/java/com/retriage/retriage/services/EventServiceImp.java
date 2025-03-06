package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EventServiceImp implements EventService {
    private static final Logger log = LoggerFactory.getLogger(EventServiceImp.class);

    private final EventRepository eventRepository;
    private final UserService userService;

    public EventServiceImp(EventRepository eventRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
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
            return false;
        }
        if(userService.getUserByEmail(event.getDirector().getEmail())==null){
            log.error("saveEvent: Event Director not found in database, Email: '{}'", event.getDirector().getEmail());
            return false;
        }
        log.debug("saveEvent: Saved new event with director email of: '{}'", event.getDirector().getEmail());
        eventRepository.save(event);
        return true;
    }
    /**
     * Retrieves an event by ID
     *
     * @param id The ID of the event
     * @return The found Event or null if not found
     */
    @Override
    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
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
        if (!eventRepository.existsById(id)) {
            return null; // Return null if event doesn't exist
        }
        event.setId(id); // Ensure the ID stays the same
        return eventRepository.save(event);
    }

    /**
     * Retrieves all events
     *
     * @return List of all events
     */
    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
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
        }
    }
}
