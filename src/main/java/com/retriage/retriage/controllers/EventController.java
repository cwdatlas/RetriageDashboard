package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.PoolType;
import com.retriage.retriage.enums.Status;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.exceptions.SuccessResponse;
import com.retriage.retriage.forms.EventTmpForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.models.PatientPoolTmp;
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

/**
 * REST controller for managing {@link Event} resources.
 * Provides API endpoints for creating, retrieving, and deleting events,
 * including managing their associated patient pools.
 * Handles cross-origin requests via {@link CrossOrigin}.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final UserService userService;

    /**
     * Constructs an instance of {@code EventController}.
     *
     * @param eventService The service for managing events.
     * @param userService  The service for managing users.
     */
    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * createEvent
     * Creates a new Event with the given form data.
     * Validates the provided {@link EventTmpForm}, creates {@link PatientPool} objects based on
     * the template data, and saves the new {@link Event}.
     * Only accessible to users with the 'Director' role.
     *
     * @param eventform The form containing the event and patient pool template data.
     * @return A {@link ResponseEntity} indicating the result of the creation.
     * Returns HTTP 201 (Created) with a {@link SuccessResponse} on success,
     * or HTTP 400 (Bad Request) with an {@link ErrorResponse} if validation fails,
     * or HTTP 500 (Internal Server Error) with an {@link ErrorResponse} if saving fails.
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('Director')") //Restricts to Director roles only
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventTmpForm eventform) {
        List<String> errorList = new ArrayList<>();
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
                    patientPool.setAutoDischarge(poolTmp.isAutoDischarge());
                    patientPool.setQueueSize(poolTmp.getQueueSize());
                    patientPool.setIcon(poolTmp.getIcon());
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
            newEvent.setPools(pools);
            newEvent.setStatus(Status.Created);
            newEvent.setStartTime(System.currentTimeMillis());
            newEvent.setDuration(eventform.getDuration());
            newEvent.setRemainingDuration(eventform.getDuration());
            newEvent.setTimeOfStatusChange(System.currentTimeMillis());
            logger.info("createEvent - Event object created: {}", newEvent);

            boolean saved = eventService.saveEvent(newEvent);
            if (saved) {
                logger.info("createEvent - Event saved successfully with ID: {}", newEvent.getId());
                // Assuming the URI for a created event would be /api/events/{id}
                return ResponseEntity.created(URI.create("/api/events/" + newEvent.getId())).body(new SuccessResponse("Successfully saved event", newEvent.getId()));
            } else {
                logger.error("createEvent - Unknown error occurred while saving event");
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
     * Retrieves an {@link Event} by its unique identifier.
     * Only accessible to users with 'Director' or 'Nurse' roles.
     *
     * @param id The ID associated to the event you are looking for.
     * @return A {@link ResponseEntity} containing the {@link Event} object if found,
     * or HTTP 404 (Not Found) with an {@link ErrorResponse} if not found.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('Director', 'Nurse')") // Restricts to Director and Nurse roles only
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
     * Returns every previously created Event.
     * Accessible to all authenticated users (implied by the absence of {@code @PreAuthorize} and controller-level security).
     *
     * @return A {@link ResponseEntity} containing a list of all {@link Event} objects,
     * or an empty list if none exist, with HTTP 200 (OK).
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK); // Returning 200 OK with the list of events
    }

    /**
     * Retrieves the currently active {@link Event}.
     * The definition of "active" is determined by the {@link EventService}.
     *
     * @return A {@link ResponseEntity} containing the active {@link Event} object if found,
     * or HTTP 404 (Not Found) if no active event exists.
     */
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
     * Deletes a specified Event by its ID.
     * Only accessible to users with the 'Director' role.
     *
     * @param id The ID associated to the event you are looking for.
     * @return A {@link ResponseEntity} indicating the result of the deletion.
     * Returns HTTP 200 (OK) on successful deletion,
     * HTTP 404 (Not Found) if the event does not exist,
     * or HTTP 500 (Internal Server Error) if deletion fails after finding the event.
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('Director')") // Restricts to Director Roles only
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        if (eventService.findEventById(id) == null) {
            logger.info("deleteEvent - Event delete failed: Event template with id {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        eventService.deleteEventById(id);
        // Verify deletion
        if (eventService.findEventById(id) != null) {
            logger.info("deleteEvent - Event failed to delete with id: {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            logger.info("deleteEvent - Event deleted with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}