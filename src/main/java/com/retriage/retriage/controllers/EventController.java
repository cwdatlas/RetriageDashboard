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
        logger.debug("Event Service injected: {}", eventService);
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
        logger.debug("createEvent - Validating Director...");
        if (eventform.getDirector() == null) {
            errorList.add("Director must  be added to event");
            logger.warn("createEvent - Director is null");
        } else if (eventform.getDirector().getEmail() == null) {
            errorList.add("createEvent - Submitted director lacking email address");
            logger.warn("createEvent - Director email is null");
        } else {
            String directorEmail = eventform.getDirector().getEmail();
            logger.debug("createEvent - Attempting to retrieve director with email: {}", directorEmail);
            User director = userService.getUserByEmail(directorEmail);
            if (director == null) {
                errorList.add("Director does not exist, not authorized to create an event");
                logger.warn("createEvent - Director with email {} not found", directorEmail);
            } else if (director.getRole() != Role.Director) {
                errorList.add("User " + director.getEmail() + " is not a director, they are a " + director.getRole());
                logger.warn("createEvent - User role: {} is not Director, Current User role: {}", director.getEmail(), director.getRole());
            } else {
                logger.debug("createEvent - Director validated!");
            }
        }
        // Patient Pool Template validation
        List<PatientPool> pools = new ArrayList<>();
        logger.info("Validating Patient Pools...");
        if (eventform.getPoolTmps().isEmpty()) {
            errorList.add("Must add at least 1 Pool Template");
            logger.debug("createEvent - No Pool Templates provided");
        } else {
            PatientPoolTmp[] templates = eventform.getPoolTmps().toArray(new PatientPoolTmp[eventform.getPoolTmps().size()]);
            logger.debug("createEvent - Processing {} Pool Templates", templates.length);
            for (PatientPoolTmp poolTmp : templates) {
                for (int i = 1; i <= poolTmp.getPoolNumber(); i++) {
                    PatientPool patientPool = new PatientPool();
                    patientPool.setPoolType(poolTmp.getPoolType());
                    patientPool.setReusable(poolTmp.isUseable());
                    patientPool.setQueueSize(poolTmp.getQueueSize());
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
                    logger.debug("createEvent - Patient Pool added: {}", patientPool);
                }
            }
        }
        // validation for event
        if (pools.isEmpty()) {
            errorList.add("Must add at least 1 Pool Template"); //TODO use dry practices to exclude this piece of code
            logger.warn("createEvent - No Patient Pools created after validation");
        } else if (errorList.isEmpty()) {
            Event newEvent = new Event();
            newEvent.setName(eventform.getName());
            User director = userService.getUserByEmail(eventform.getDirector().getEmail());
            newEvent.setDirector(director);
            newEvent.setNurses(new ArrayList<User>());
            newEvent.setPools(pools);
            newEvent.setStatus(Status.Paused);
            newEvent.setStartTime(System.currentTimeMillis());
            newEvent.setDuration(eventform.getDuration());
            newEvent.setRemainingDuration(eventform.getDuration());
            newEvent.setTimeOfStatusChange(System.currentTimeMillis());
            logger.info("createEvent - Event object created: {}", newEvent);

            boolean saved = eventService.saveEvent(newEvent);
            if (saved) {
                logger.info("createEvent - Event saved successfully");
                return ResponseEntity.created(URI.create("/events/")).body(new SuccessResponse("Successfully saved event", newEvent.getId()));
            } else {
                logger.error("createEvent - Unknown error occurred while saving event");
                ErrorResponse errorResponse = new ErrorResponse(List.of("Unknown error saving event."), HttpStatus.INTERNAL_SERVER_ERROR.value(), "SAVE_FAILED");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
            logger.warn("createEvent - Returning response with errors: {}", errorList);
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
            logger.warn("findEventByID - Event with id {} not found", id);
            ErrorResponse errorResponse = new ErrorResponse(List.of("Event with id " + id + " not found."), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
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
        logger.debug("Returning {} events", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK); // Returning 200 OK with the list of events
    }

    @Transactional
    @GetMapping(value = "/active", produces = "application/json")
    public ResponseEntity<Event> getActiveEvent() {
        Event event = eventService.findActiveEvent();
        if (event == null) {
            logger.warn("getActiveEvent - No active event found, returning HTTP STATUS 404");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        logger.debug("getActiveEvent - Active event found, returning HTTP STATUS ok");
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
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
        logger.info("Event with id {} deleted", id);
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
                errorList.add("Nurse of name of " + nurse.getFirstName() + " " + nurse.getLastName() + " must have email.");
                logger.warn("createEvent - submitted Nurse of name {} did not have an email address.", nurse.getFirstName() + " " + nurse.getLastName());
            } else {
                String nurseEmail = nurse.getEmail();
                User savedNurse = userService.getUserByEmail(nurseEmail);
                if (savedNurse == null) {
                    errorList.add("Submitted nurse " + nurse.getEmail() + "was not found.");
                    logger.warn("updateEvent - Nurse with email {} not found", nurseEmail);
                } else if (savedNurse.getRole() == Role.Guest) {
                    errorList.add("User " + savedNurse.getEmail() + " is a Guest, Guests can't be added to an event");
                    logger.warn("createEvent - User {} is a Guest", nurse.getEmail());
                } else {
                    nurseList.add(savedNurse);
                    logger.warn("createEvent - Nurse {} validated", savedNurse.getEmail());
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
        updatedEvent.setRemainingDuration(eventForm.getRemainingDuration());
        Event oldEvent = eventService.findEventById(eventForm.getId());
        if(oldEvent == null){
            errorList.add("Attempted to update event without already existing.");
        }else if(oldEvent.getStatus() == eventForm.getStatus()){
            updatedEvent.setTimeOfStatusChange(eventForm.getTimeOfStatusChange());
        }
        logger.debug("updateEvent - Updated Event object created: {}", updatedEvent);

        Event response = eventService.updateEvent(eventForm.getId(), updatedEvent);
        if (response == null) {
            logger.warn("updateEvent - Unable to update event with id: {}", eventForm.getId());
            ErrorResponse errorResponse = new ErrorResponse(List.of("Event with id " + eventForm.getId() + " unable to be updated."), HttpStatus.NOT_FOUND.value(), "EVENT_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new SuccessResponse("Updated event successfully", response.getId())); // Using ResponseEntity.ok for success with JSON
    }
}