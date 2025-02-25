package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;
import com.retriage.retriage.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EventServiceImp implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImp(EventRepository eventRepository) {
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
            return false;
        }
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
