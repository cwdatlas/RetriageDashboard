package com.retriage.retriage.controllers;


import com.retriage.retriage.enums.Role;
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
        logger.debug("WebSocketConnection: Client tried to get data");
        List<String> errorList = new ArrayList<>();
        List<User> nurseList = new ArrayList<>();
        // Validate nurses
        for (User nurse : eventForm.getNurses()) {
            if (nurse.getEmail() == null) {
                errorList.add("Nurse of name of " + nurse.getFirstName() + " " + nurse.getLastName() + " must have email.");
                logger.debug("createEvent - submitted Nurse of name {} did not have an email address.", nurse.getFirstName() + " " + nurse.getLastName());
            } else {
                String nurseEmail = nurse.getEmail();
                User savedNurse = userService.getUserByEmail(nurseEmail);
                if (savedNurse == null) {
                    errorList.add("Submitted nurse " + nurse.getEmail() + "was not found.");
                    logger.debug("updateEvent - Nurse with email {} not found", nurseEmail);
                } else if (savedNurse.getRole() == Role.Guest) {
                    errorList.add("User " + savedNurse.getEmail() + " is a Guest, Guests can't be added to an event");
                    logger.info("createEvent - User {} is a Guest", nurse.getEmail());
                } else {
                    nurseList.add(savedNurse);
                    logger.debug("createEvent - Nurse {} validated", savedNurse.getEmail());
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

        eventService.updateEvent(eventForm.getId(), updatedEvent);

        Event response = eventService.findActiveEvent();
        if (response == null) {
            logger.debug("EventWebSocketController: Zero running events found");
            Event errorReturnEvent = new Event();
            errorReturnEvent.setName("NoEventFound");
            return errorReturnEvent;
        }
        return response;
    }
}
