package com.retriage.retriage.controllers;


import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@Controller
public class EventWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(EventWebSocketController.class);
    private final EventService eventService;

    EventWebSocketController(EventService eventService) {
        this.eventService = eventService;
    }


    @MessageMapping("/active_event/")
    @SendTo("/topic/event_updates")
    public Event WebsocketConnection(EventForm eventForm) {
        logger.debug("WebSocketConnection: Client tried to get data");
        //TODO: Validation for updated event
        Event updatedEvent = new Event();
        updatedEvent.setName(eventForm.getName());
        updatedEvent.setId(eventForm.getId());
        updatedEvent.setDuration(eventForm.getDuration());
        updatedEvent.setPools(eventForm.getPools());
        updatedEvent.setStatus(eventForm.getStatus());
        updatedEvent.setNurses(eventForm.getNurses());
        updatedEvent.setDirector(eventForm.getDirector());
        updatedEvent.setStartTime(eventForm.getStartTime());

        eventService.updateEvent(eventForm.getId(), updatedEvent);

        //Return one or null active events
        Event response = eventService.findActiveEvent();
        if (response != null) {
            logger.debug("EventWebSocketController: Zero running events found");
        }
        return response;
    }
}
