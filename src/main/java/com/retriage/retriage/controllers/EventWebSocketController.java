package com.retriage.retriage.controllers;


import com.retriage.retriage.enums.Status;
import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.services.EventService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Controller
public class EventWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(EventWebSocketController.class);
    private final EventService eventService;

    EventWebSocketController(EventService eventService) {
        this.eventService = eventService;
    }

    @Transactional
    @MessageMapping("/update")
    @SendTo("/topic/event_updates")
    public Event WebsocketConnection(EventForm eventForm) {
        List<String> errorList = new ArrayList<>();

        //TODO: Validation for updated event
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
            logger.warn("WebsocketConnection - Active event find failed: No active event found.");
            Event errorReturnEvent = new Event();
            errorReturnEvent.setName("NoEventFound");
            return errorReturnEvent;
        }
        logger.info("WebsocketConnection - Active event found.");
        return response;
    }
}
