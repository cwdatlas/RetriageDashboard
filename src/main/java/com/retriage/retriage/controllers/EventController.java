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
        logger.debug("EventController initialized with EventService and UserService");
    }

    /**
     * createEvent
     * Creates a new Event with the given form data
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventTmpForm eventform) {
        List<String> errorList = new ArrayList<>();

        if (eventform.getDirector() == null || eventform.getDirector().getEmail() == null) {
            errorList.add("Director must be specified with a valid email");
            logger.warn("createEvent - Invalid Director email input");
        } else {
            String directorEmail = eventform.getDirector().getEmail();
            User director = userService.getUserByEmail(directorEmail);

            if (director == null) {
                errorList.add("Director does not exist");
                logger.warn("createEvent - Director with email {} not found", directorEmail);
            } else if (director.getRole() != Role.Director) {
                errorList.add("User is not a director");
                logger.warn("createEvent - User {} is not a Director (Role: {})", director.getEmail(), director.getRole());
            }
        }

        List<PatientPool> pools = new ArrayList<>();
        if (eventform.getPoolTmps().isEmpty()) {
            errorList.add("At least one Pool Template is required");
            logger.warn("createEvent - No Pool Templates provided");
        } else {
            for (PatientPoolTmp poolTmp : eventform.getPoolTmps()) {
                for (int i = 1; i <= poolTmp.getPoolNumber(); i++) {
                    PatientPool patientPool = new PatientPool();
                    patientPool.setPoolType(poolTmp.getPoolType());
                    patientPool.setUseable(poolTmp.isUseable());
                    patientPool.setProcessTime(poolTmp.getPoolType() == PoolType.Bay ? eventform.getDuration() : poolTmp.getProcessTime());
                    patientPool.setName(poolTmp.getPoolNumber() == 1 ? poolTmp.getName() : poolTmp.getName() + " " + i);
                    patientPool.setActive(true);
                    pools.add(patientPool);
                }
            }
            logger.debug("createEvent - Created {} Patient Pools", pools.size());
        }

        if (!errorList.isEmpty()) {
            logger.warn("createEvent - Validation failed with errors: {}", errorList);
            return new ResponseEntity<>(new ErrorResponse(errorList, HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED"), HttpStatus.BAD_REQUEST);
        }

        Event newEvent = new Event();
        newEvent.setName(eventform.getName());
        newEvent.setDirector(userService.getUserByEmail(eventform.getDirector().getEmail()));
        newEvent.setNurses(new ArrayList<>());
        newEvent.setPools(pools);
        newEvent.setStatus(Status.Paused);
        newEvent.setStartTime(System.currentTimeMillis());
        newEvent.setDuration(eventform.getDuration());

        boolean saved = eventService.saveEvent(newEvent);
        if (saved) {
            logger.debug("createEvent - Event '{}' created successfully", newEvent.getName());
            return ResponseEntity.created(URI.create("/events/")).body(new SuccessResponse("Successfully saved event", newEvent.getId()));
        } else {
            logger.error("createEvent - Failed to save event due to unknown error");
            return new ResponseEntity<>(new ErrorResponse(List.of("Unknown error saving event."), HttpStatus.INTERNAL_SERVER_ERROR.value(), "SAVE_FAILED"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * findEventByID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findEventByID(@PathVariable Long id) {
        Event event = eventService.findEventById(id);
        if (event == null) {
            logger.warn("findEventByID - Event with id {} not found", id);
            return new ResponseEntity<>(new ErrorResponse(List.of("Event not found"), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(event);
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
        logger.debug("getAllEvents - Found {} events", events.size());
        return ResponseEntity.ok(events);
    }

    @Transactional
    @GetMapping(value = "/active", produces = "application/json")
    public ResponseEntity<Event> getActiveEvent() {
        Event event = eventService.findActiveEvent();
        if (event == null) {
            logger.warn("getActiveEvent - No active event found");
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Director')") //Restricts to Director roles only
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
        logger.info("deleteEvent - Event {} deleted successfully", id);
        return ResponseEntity.ok().build();
    }

    /**
     * updateEvent
     */
    @PutMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('Director')") //Restricts to Director roles only
    public ResponseEntity<?> updateEvent(@Valid @RequestBody EventForm eventForm) {
        List<String> errorList = new ArrayList<>();
        List<User> nurseList = new ArrayList<>();

        for (User nurse : eventForm.getNurses()) {
            if (nurse.getEmail() == null) {
                errorList.add("Nurse must have an email");
                logger.warn("updateEvent - Nurse {} is missing an email", nurse.getFirstName() + " " + nurse.getLastName());
            } else {
                User savedNurse = userService.getUserByEmail(nurse.getEmail());
                if (savedNurse == null) {
                    errorList.add("Nurse not found");
                    logger.warn("updateEvent - Nurse with email {} not found", nurse.getEmail());
                } else if (savedNurse.getRole() == Role.Guest) {
                    errorList.add("Guest users cannot be nurses");
                    logger.warn("updateEvent - User {} is a Guest and cannot be added", nurse.getEmail());
                } else {
                    nurseList.add(savedNurse);
                }
            }
        }

        if (!errorList.isEmpty()) {
            logger.warn("updateEvent - Validation errors: {}", errorList);
            return new ResponseEntity<>(new ErrorResponse(errorList, HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED"), HttpStatus.BAD_REQUEST);
        }

        Event updatedEvent = new Event();
        updatedEvent.setId(eventForm.getId());
        updatedEvent.setName(eventForm.getName());
        updatedEvent.setDuration(eventForm.getDuration());
        updatedEvent.setPools(eventForm.getPools());
        updatedEvent.setStatus(eventForm.getStatus());
        updatedEvent.setNurses(nurseList);
        updatedEvent.setDirector(eventForm.getDirector());
        updatedEvent.setStartTime(eventForm.getStartTime());

        Event response = eventService.updateEvent(eventForm.getId(), updatedEvent);
        if (response == null) {
            logger.warn("updateEvent - Update failed for event {}", eventForm.getId());
            return new ResponseEntity<>(new ErrorResponse(List.of("Event update failed"), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
        logger.info("updateEvent - Event {} updated successfully", response.getId());
        return ResponseEntity.ok(new SuccessResponse("Event updated successfully", response.getId()));
    }
}