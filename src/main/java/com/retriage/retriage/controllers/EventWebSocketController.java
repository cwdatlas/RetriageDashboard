package com.retriage.retriage.controllers;


import com.retriage.retriage.enums.Status;
import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.ResponseWrapper;
import com.retriage.retriage.services.EventService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket controller for handling real-time updates related to {@link Event} objects.
 * Receives event updates from clients and broadcasts the latest active event status
 * to subscribed clients.
 */
@Controller
public class EventWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(EventWebSocketController.class);
    private final EventService eventService;

    /**
     * Constructs an instance of {@code EventWebSocketController}.
     *
     * @param eventService The service for managing events.
     */
    EventWebSocketController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Handles incoming WebSocket messages to update an event.
     * Expects an {@link EventForm} containing the updated event details.
     * Processes the update, checks for conflicts with other running events,
     * updates the event status (including resetting if changing from Ended to Running),
     * and broadcasts the currently active event to the {@code /topic/event_updates} destination.
     *
     * @param eventForm The form containing the updated event data sent via WebSocket.
     * @return A {@link ResponseWrapper} containing the updated active {@link Event}
     * and a status message, or an error response if the update fails or
     * a conflict occurs (e.g., trying to start an event when another is already running).
     */
    @Transactional
    @MessageMapping("/update")
    @SendTo("/topic/event_updates")
    public ResponseWrapper WebsocketConnection(EventForm eventForm) {
        if (eventForm.getStatus() == Status.Running) {
            Event activeEvent = eventService.findActiveEvent();
            if (activeEvent != null && !activeEvent.getId().equals(eventForm.getId())) {
                return new ResponseWrapper<Void>(HttpStatus.BAD_REQUEST.value(), "Another event is already running.", null);
            }
        }
        List<String> errorList = new ArrayList<>();

        Event updatedEvent = new Event();
        updatedEvent.setName(eventForm.getName());
        updatedEvent.setId(eventForm.getId());
        updatedEvent.setDuration(eventForm.getDuration());
        updatedEvent.setPools(eventForm.getPools());
        updatedEvent.setStatus(eventForm.getStatus());
        updatedEvent.setStartTime(eventForm.getStartTime());
        updatedEvent.setRemainingDuration(eventForm.getRemainingDuration());
        Event oldEvent = eventService.findEventById(eventForm.getId());
        if (oldEvent == null) {
            errorList.add("Attempted to update event without already existing.");
        } else if (oldEvent.getStatus() != eventForm.getStatus()) {
            if (oldEvent.getStatus() == Status.Ended && eventForm.getStatus() == Status.Running) {
                updatedEvent = eventService.resetEventById(oldEvent);
                updatedEvent.setStatus(Status.Running);
            }
            updatedEvent.setTimeOfStatusChange(System.currentTimeMillis());
        } else {
            updatedEvent.setTimeOfStatusChange(eventForm.getTimeOfStatusChange());
        }


        eventService.updateEvent(eventForm.getId(), updatedEvent);

        Event response = eventService.findActiveEvent();
        if (response == null) {
            logger.debug("WebsocketConnection - Active event find failed: No active event found.");
            return new ResponseWrapper<Void>(HttpStatus.NOT_FOUND.value(), "There is not an event running currently.", null);

        }
        logger.info("WebsocketConnection - Active event found.");
        return new ResponseWrapper<Event>(HttpStatus.OK.value(), "Nominal Event Update", response);
    }
}