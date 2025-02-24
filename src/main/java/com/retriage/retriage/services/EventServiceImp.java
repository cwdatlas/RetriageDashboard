package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;
import com.retriage.retriage.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EventServiceImp implements EventService {

    private final EventRepository eventRepository;

    EventServiceImp(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public boolean saveEvent(Event event) {
        return false;
    }

    @Override
    public Event findEventById(Long id) {
        return new Event();
    }

    @Override
    public Event UpdateEvent(long id, Event event) {
        return null;
    }

    @Override
    public List<Event> findAllEvents() {
        return List.of();
    }

    @Override
    public boolean deleteEventById(Long id) {
        return false;
    }
}
