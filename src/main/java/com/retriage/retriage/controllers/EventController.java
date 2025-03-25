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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        logger.info("Entering EventController constructor");
        this.eventService = eventService;
        logger.info("Event Service injected: {}", eventService);
        this.userService = userService;
        logger.info("User Service injected: {}", userService);
        logger.info("Exiting EventController constructor");
    }

    /**
     * createEvent
     * Creates a new Event with the given form data
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventTmpForm eventform) {
        logger.info("Entering createEvent with eventForm: {}", eventform);
        List<String> errorList = new ArrayList<>();
        // Validate Director
        logger.info("Validating Director...");
        if (eventform.getDirector() == null) {
            errorList.add("Director must  be added to event");
            logger.warn("createEvent - Director is null");
        } else if (eventform.getDirector().getEmail() == null) {
            errorList.add("Submitted director lacking email address");
            logger.warn("createEvent - Director email is null");
        } else {
            String directorEmail = eventform.getDirector().getEmail();
            logger.info("createEvent - Attempting to retrieve director with email: {}", directorEmail);
            User director = userService.getUserByEmail(directorEmail);
            if (director == null) {
                errorList.add("Director does not exist, not authorized to create an event");
                logger.warn("createEvent - Director with email {} not found", directorEmail);
            } else if (director.getRole() != Role.Director) {
                errorList.add("User " + director.getEmail() + " is not a director, they are a " + director.getRole());
                logger.warn("createEvent - User {} is not a Director, role is {}", director.getEmail(), director.getRole());
            } else {
                logger.info("createEvent - Director {} validated", director.getEmail());
            }
        }
        logger.info("Director Validation Complete!");

        // Patient Pool Template validation
        List<PatientPool> pools = new ArrayList<>();
        logger.info("Validating Patient Pools...");
        if (eventform.getPoolTmps().isEmpty()) {
            errorList.add("Must add at least 1 Pool Template");
            logger.warn("createEvent - No Pool Templates provided");
        } else {
            PatientPoolTmp[] templates = eventform.getPoolTmps().toArray(new PatientPoolTmp[eventform.getPoolTmps().size()]);
            logger.info("createEvent - Processing {} Pool Templates", templates.length);
            for (PatientPoolTmp poolTmp : templates) {
                logger.debug("createEvent - Processing Pool Template: {}", poolTmp);
                for (int i = 1; i <= poolTmp.getPoolNumber(); i++) {
                    PatientPool patientPool = new PatientPool();
                    patientPool.setPoolType(poolTmp.getPoolType());
                    patientPool.setUseable(poolTmp.isUseable());
                    if (poolTmp.getPoolType() == PoolType.Bay) {
                        patientPool.setProcessTime(eventform.getDuration());
                        logger.debug("createEvent - Bay Pool created, process time set to event duration: {}", eventform.getDuration());
                    } else {
                        patientPool.setProcessTime(poolTmp.getProcessTime());
                        logger.debug("createEvent - Non-Bay Pool created, process time set to: {}", poolTmp.getProcessTime());
                    }
                    if (poolTmp.getPoolNumber() == 1) {
                        patientPool.setName(poolTmp.getName());
                    } else {
                        patientPool.setName(poolTmp.getName() + " " + i);
                    }
                    patientPool.setPatients(new ArrayList<Patient>());
                    patientPool.setActive(true);
                    pools.add(patientPool);
                    logger.debug("createEvent - Patient Pool added: {}", patientPool);
                }
            }
        }
        logger.info("Patient Pool Validation Complete!");
        // validation for event
        if (pools.isEmpty()) {
            errorList.add("Must add at least 1 Pool Template"); //TODO use dry practices to exclude this piece of code
            logger.warn("createEvent - No Patient Pools created after validation");
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
            logger.info("createEvent - Event object created: {}", newEvent);

            //Saving the event
            boolean saved = eventService.saveEvent(newEvent);
            logger.info("createEvent - Event saved: {}", saved);
            //Error handling (very basic)
            if (saved) {
                logger.info("Exiting createEvent, event saved successfully");
                return ResponseEntity.created(URI.create("/events/")).body(new SuccessResponse("Successfully saved event", newEvent.getId()));
            } else {
                logger.error("createEvent - Unknown error occurred while saving event");
                ErrorResponse errorResponse = new ErrorResponse("Unknown error saving event.", HttpStatus.INTERNAL_SERVER_ERROR.value(), "SAVE_FAILED");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        logger.info("Exiting createEvent, returning response with errors: {}", errorList);
        return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
    }

    /**
     * findEventByID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findEventByID(@PathVariable Long id) {
        logger.info("Entering findEventByID with id: {}", id);
        try {
            Event event = eventService.findEventById(id);
            if (event == null) {
                logger.warn("findEventByID - Event with id {} not found", id);
                ErrorResponse errorResponse = new ErrorResponse("Event with id " + id + " not found.", HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            logger.info("Exiting findEventByID with event: {}", event);
            return ResponseEntity.ok(event); // Returning 200 OK with the Event object as JSON
        } catch (Exception e) {
            logger.error("findEventByID - Error finding event with id {}: {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Error finding event with id " + id + ".", HttpStatus.INTERNAL_SERVER_ERROR.value(), "FIND_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getAllEvents
     * Returns every previously created Event
     * @return The list of every event
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllEvents() {
        logger.info("Entering getAllEvents");
        try {
            List<Event> events = eventService.findAllEvents();
            logger.info("Exiting getAllEvents, returning {} events", events.size());
            return new ResponseEntity<>(events, HttpStatus.OK); // Returning 200 OK with the list of events
        } catch (Exception e) {
            logger.error("getAllEvents - Error retrieving all events: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving all events.", HttpStatus.INTERNAL_SERVER_ERROR.value(), "FIND_ALL_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * deleteEvent
     * Deletes a specified Event by first finding the passed ID, then deleting it
     * @param id The ID associated to the event you are looking for
     * @return Returns a confirmation that the event was deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        logger.info("Entering deleteEvent with id: {}", id);
        try {
            eventService.deleteEventById(id);
            logger.info("Exiting deleteEvent, event with id {} deleted", id);
            return ResponseEntity.status(HttpStatus.OK).build(); // Successful deletion, returns 200 OK with no body
        } catch (EmptyResultDataAccessException e) {
            logger.warn("deleteEvent - Event with id {} not found", id);
            ErrorResponse errorResponse = new ErrorResponse("Event with id " + id + " not found.", HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("deleteEvent - Error deleting event with id {}: {}", id, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Failed to delete event with id " + id + " due to an internal error.", HttpStatus.INTERNAL_SERVER_ERROR.value(), "DELETE_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *  updateEvent
     *
     */
    @PutMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateEvent(@Valid @RequestBody EventForm eventForm) {
        logger.info("Entering updateEvent with eventForm: {}", eventForm);
        //secondary validation

        Event updatedEvent = new Event();
        updatedEvent.setName(eventForm.getName());
        updatedEvent.setId(eventForm.getId());
        updatedEvent.setDuration(eventForm.getDuration());
        updatedEvent.setPools(eventForm.getPools());
        updatedEvent.setStatus(eventForm.getStatus());
        updatedEvent.setNurses(eventForm.getNurses());
        updatedEvent.setDirector(eventForm.getDirector());
        updatedEvent.setStartTime(eventForm.getStartTime());
        logger.debug("updateEvent - Updated Event object created: {}", updatedEvent);

        try {
            Event response = eventService.updateEvent(eventForm.getId(), updatedEvent);
            if (response == null) {
                logger.warn("updateEvent - Unable to update event with id: {}", eventForm.getId());
                ErrorResponse errorResponse = new ErrorResponse("Unable to update event with id: " + eventForm.getId(), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            logger.info("Exiting updateEvent, event with id {} updated successfully", eventForm.getId());
            return ResponseEntity.ok(new SuccessResponse("Updated event successfully", response.getId())); // Using ResponseEntity.ok for success with JSON
        } catch (IllegalArgumentException e) {
            logger.error("updateEvent - IllegalArgumentException: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), "INVALID_INPUT");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("updateEvent - Exception during update:", e);
            ErrorResponse errorResponse = new ErrorResponse("Failed to update event due to an internal error.", HttpStatus.INTERNAL_SERVER_ERROR.value(), "UPDATE_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
}