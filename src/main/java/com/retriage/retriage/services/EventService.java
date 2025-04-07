package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;

import java.util.List;

/**
 *
 */
//TODO add all CRUD functions. Create Read Update and Delete. Some are already here, but more needs to be done
public interface EventService {
    boolean saveEvent(Event event);

    Event findEventById(Long id);

    Event updateEvent(long id, Event event);

    List<Event> findAllEvents();

    void deleteEventById(Long id);

    Event findActiveEvent();

    Event resetEventById(Event event);
}
