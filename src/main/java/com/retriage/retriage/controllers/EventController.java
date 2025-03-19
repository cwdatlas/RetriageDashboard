package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.EventService;
import com.retriage.retriage.services.UserService;
import jakarta.validation.Valid;
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
    /**
     *
     */
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
     * 1) Create a new User
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventForm eventform) {
        //Secondary validation...
        List<String> errorList = new ArrayList<>();
        // Validate Director
        if (eventform.getDirector() == null) {
            errorList.add("Director must  be added to event");
        } else if (eventform.getDirector().getEmail() == null) {
            errorList.add("Submitted director lacking email address");
        } else {
            User director = userService.getUserByEmail(eventform.getDirector().getEmail());
            if (director.getRole() != Role.Director) {
                errorList.add("User " + director.getEmail() + " is not a director, they are a " + director.getRole());
            }
        }
        // Validate Nurses
        List<User> nurses = new ArrayList<>();
        for (User nurse : eventform.getNurses()) {
            if (nurse.getEmail() == null) {
                errorList.add("User " + nurse.getFirstName() + " does not have an email");
            } else {
                User savedNurse = userService.getUserByEmail(nurse.getEmail());
                if (savedNurse == null) {
                    errorList.add("User " + nurse.getEmail() + " could not be found. They must have logged in at least once.");
                } else if (savedNurse.getRole() == null || savedNurse.getRole() == Role.Guest) { //TODO make this inclusive so it can scale with additional roles
                    errorList.add("User " + nurse.getEmail() + " is not a nurse or a director");
                } else {
                    nurses.add(savedNurse);
                }
            }
        }


        //Creating the event
        if (errorList.isEmpty()) {
            Event newEvent = new Event();
            newEvent.setId(eventform.getId());
            newEvent.setName(eventform.getName());
            User director = userService.getUserByEmail(eventform.getDirector().getEmail());
            newEvent.setDirector(director);
            newEvent.setNurses(nurses);
            newEvent.setPools(eventform.getPools());
            newEvent.setStatus(eventform.getStatus());
            newEvent.setStartTime(eventform.getStartTime());
            newEvent.setEndTime(eventform.getEndTime());

            //Saving the event
            boolean saved = eventService.saveEvent(newEvent);
            //Error handling (very basic)
            if (saved) {
                errorList.add("Successfully saved event");
            }else{
                errorList.add("Unknown error saving event");
            }

        }
        return ResponseEntity.
                created(URI.create("/events/"))
                .body(errorList);
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */

    @GetMapping(value = "/usr/{id}", produces = "application/json")
    public ResponseEntity<Event> findUserByID(@PathVariable Long id) {
        Event event = eventService.findEventById(id);
        return ResponseEntity.created(URI.create("/events/" + id)).body(event);
    }


    @GetMapping(produces = "application/json")
    public List<Event> getAllEvents() {
        return eventService.findAllEvents();
    }

    /**
     * 4) Delete an Event
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 5) Update an existing Event
     * PUT /events/{id}
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event updatedEvent = eventService.UpdateEvent(id, event);
        if (updatedEvent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedEvent);
    }

}
