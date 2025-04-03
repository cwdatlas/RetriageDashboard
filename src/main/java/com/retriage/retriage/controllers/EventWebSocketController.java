package com.retriage.retriage.controllers;


import com.retriage.retriage.enums.Role;
import com.retriage.retriage.enums.Status;
import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.EventService;
import com.retriage.retriage.services.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;

    EventWebSocketController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @Transactional
    @MessageMapping("/update")
    @SendTo("/topic/event_updates")
    public Event WebsocketConnection(EventForm eventForm) {
        List<String> errorList = new ArrayList<>();
        List<User> nurseList = new ArrayList<>();
        // Validate nurses
        for (User nurse : eventForm.getNurses()) {
            if (nurse.getEmail() == null) {
                errorList.add("Nurse must have a valid email.");
                logger.warn("WebsocketConnection - Nurse validation failed: Invalid email.");
            } else {
                String nurseEmail = nurse.getEmail();
                User savedNurse = userService.getUserByEmail(nurseEmail);
                if (savedNurse == null || savedNurse.getRole() == Role.Guest) {
                    errorList.add("Nurse not found or is a Guest.");
                    logger.warn("WebsocketConnection - Nurse validation failed: Nurse not found or Guest.");
                } else {
                    nurseList.add(savedNurse);
                }
            }
        }
        //TODO: Validation for updated event
        Event updatedEvent = new Event();
        updatedEvent.setName(eventForm.getName());
        updatedEvent.setId(eventForm.getId());
        updatedEvent.setDuration(eventForm.getDuration());
        updatedEvent.setPools(eventForm.getPools());
        updatedEvent.setStatus(eventForm.getStatus());
        updatedEvent.setNurses(nurseList);
        updatedEvent.setDirector(eventForm.getDirector());
        updatedEvent.setStartTime(eventForm.getStartTime());
        updatedEvent.setRemainingDuration(eventForm.getRemainingDuration());
        Event oldEvent = eventService.findEventById(eventForm.getId());
        if(oldEvent == null){
            errorList.add("Attempted to update event without already existing.");
        }else if(oldEvent.getStatus() != eventForm.getStatus()){
            updatedEvent.setTimeOfStatusChange(System.currentTimeMillis());
        }else{
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
