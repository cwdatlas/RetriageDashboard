package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.PoolType;
import com.retriage.retriage.enums.Role;
import com.retriage.retriage.enums.Status;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.exceptions.SuccessResponse;
import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.forms.EventTmpForm;
import com.retriage.retriage.models.*;
import com.retriage.retriage.services.EventService;
import com.retriage.retriage.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final UserService userService;

    /**
     * Constructor injection of the service
     */
    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * createEvent
     * Creates a new Event with the given form data
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('ADMIN')") //Restricts to ADMIN role only
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventTmpForm eventform) {
        List<String> errorList = new ArrayList<>();
        // Validate Director
        if (eventform.getDirector() == null || eventform.getDirector().getEmail() == null) {
            errorList.add("Director must be added to event with a valid email.");
            logger.warn("createEvent - Director validation failed: Invalid director data.");
        } else {
            String directorEmail = eventform.getDirector().getEmail();
            User director = userService.getUserByEmail(directorEmail);
            if (director == null || director.getRole() != Role.Director) {
                errorList.add("Director does not exist or is not authorized.");
                logger.warn("createEvent - Director validation failed: Director not found or unauthorized.");
            }
        }
        // Patient Pool Template validation
        List<PatientPool> pools = new ArrayList<>();
        if (eventform.getPoolTmps().isEmpty()) {
            errorList.add("Must add at least 1 Pool Template.");
            logger.warn("createEvent - Pool template validation failed: No pool templates provided.");
        } else {
            PatientPoolTmp[] templates = eventform.getPoolTmps().toArray(new PatientPoolTmp[eventform.getPoolTmps().size()]);
            for (PatientPoolTmp poolTmp : templates) {
                for (int i = 1; i <= poolTmp.getPoolNumber(); i++) {
                    PatientPool patientPool = new PatientPool();
                    patientPool.setPoolType(poolTmp.getPoolType());
                    patientPool.setUseable(poolTmp.isUseable());
                    patientPool.setQueueSize(poolTmp.getQueueSize());
                    patientPool.setProcessTime(poolTmp.getPoolType() == PoolType.Bay ? eventform.getDuration() : poolTmp.getProcessTime());
                    patientPool.setName(poolTmp.getPoolNumber() == 1 ? poolTmp.getName() : poolTmp.getName() + " " + i);
                    patientPool.setPatients(new ArrayList<>());
                    patientPool.setActive(true);
                    pools.add(patientPool);
                }
            }
        }
        // validation for event
        if (pools.isEmpty()) {
            errorList.add("Must add at least 1 Pool Template.");
            logger.warn("createEvent - Event creation failed: No valid pools created.");
        } else if (errorList.isEmpty()) {
            Event newEvent = new Event();
            newEvent.setName(eventform.getName());
            User director = userService.getUserByEmail(eventform.getDirector().getEmail());
            newEvent.setDirector(director);
            newEvent.setNurses(new ArrayList<>());
            newEvent.setPools(pools);
            newEvent.setStatus(Status.Paused);
            newEvent.setStartTime(System.currentTimeMillis());
            newEvent.setDuration(eventform.getDuration());

            boolean saved = eventService.saveEvent(newEvent);
            if (saved) {
                logger.info("createEvent - Event saved successfully with ID: {}", newEvent.getId());
                return ResponseEntity.created(URI.create("/events/")).body(new SuccessResponse("Successfully saved event", newEvent.getId()));
            } else {
                logger.error("createEvent - Event save failed: Unknown error.");
                ErrorResponse errorResponse = new ErrorResponse(List.of("Unknown error saving event."), HttpStatus.INTERNAL_SERVER_ERROR.value(), "SAVE_FAILED");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        logger.warn("createEvent - Event creation failed: Validation errors - {}", errorList);
        ErrorResponse errorResponse = new ErrorResponse(errorList, HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * findEventByID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findEventByID(@PathVariable Long id) {
        Event event = eventService.findEventById(id);
        if (event == null) {
            logger.warn("findEventByID - Event find failed: Event with id {} not found.", id);
            ErrorResponse errorResponse = new ErrorResponse(List.of("Event with id " + id + " not found."), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        logger.info("findEventByID - Event found with id: {}", id);
        return ResponseEntity.ok(event); // Returning 200 OK with the Event object as JSON
    }

    /**
     * getAllEvents
     * Returns every previously created Event
     *
     * @return The list of every event
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK); // Returning 200 OK with the list of events
    }

    @Transactional
    @GetMapping(value = "/active", produces = "application/json")
    public ResponseEntity<Event> getActiveEvent() {
        Event event = eventService.findActiveEvent();
        if (event == null) {
            logger.warn("getActiveEvent - Event find failed: No active event found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(event);
    }

    /**
     * deleteEvent
     * Deletes a specified Event by first finding the passed ID, then deleting it
     *
     * @param id The ID associated to the event you are looking for
     * @return Returns a confirmation that the event was deleted
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
        logger.info("deleteEvent - Event deleted with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).build(); // Successful deletion
    }

    /**
     * updateEvent
     */
    @PutMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateEvent(@Valid @RequestBody EventForm eventForm) {
        List<String> errorList = new ArrayList<>();
        List<User> nurseList = new ArrayList<>();
        // Validate Director
        for (User nurse : eventForm.getNurses()) {
            if (nurse.getEmail() == null) {
                errorList.add("Nurse must have a valid email.");
                logger.warn("updateEvent - Nurse validation failed: Invalid email.");
            } else {
                String nurseEmail = nurse.getEmail();
                User savedNurse = userService.getUserByEmail(nurseEmail);
                if (savedNurse == null || savedNurse.getRole() == Role.Guest) {
                    errorList.add("Nurse not found or is a Guest.");
                    logger.warn("updateEvent - Nurse validation failed: Nurse not found or Guest.");
                } else {
                    nurseList.add(savedNurse);
                }
            }
        }

        Event updatedEvent = new Event();
        updatedEvent.setName(eventForm.getName());
        updatedEvent.setId(eventForm.getId());
        updatedEvent.setDuration(eventForm.getDuration());
        updatedEvent.setPools(eventForm.getPools());
        updatedEvent.setStatus(eventForm.getStatus());
        updatedEvent.setNurses(nurseList);
        updatedEvent.setDirector(eventForm.getDirector());
        updatedEvent.setStartTime(eventForm.getStartTime());

        Event response = eventService.updateEvent(eventForm.getId(), updatedEvent);
        if (response == null) {
            logger.warn("updateEvent - Event update failed: Event with id {} not found.", eventForm.getId());
            ErrorResponse errorResponse = new ErrorResponse(List.of("Event with id " + eventForm.getId() + " unable to be updated."), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        logger.info("updateEvent - Event updated successfully with id: {}", response.getId());
        return ResponseEntity.ok(new SuccessResponse("Updated event successfully", response.getId())); // Using ResponseEntity.ok for success with JSON
    }
}