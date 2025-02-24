package com.retriage.retriage.controllers;

import com.retriage.retriage.forms.EventForm;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.services.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/events")
public class EventrController {
    /**
     *
     */
    private final EventService eventService;

    /**
     * Constructor injection of the service
     */
    public EventrController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * 1) Create a new User
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createEvent(@Valid @RequestBody EventForm eventform) {
        //Secondary validation...

        //Creating the event
        Event newEvent = new Event();
        newEvent.setName(eventform.getName());
        newEvent.setDirector(eventform.getDirector());
        newEvent.setNurses(eventform.getNurses());
        newEvent.setResources(eventform.getResources());
        newEvent.setStatus(eventform.getStatus());
        newEvent.setStartTime(eventform.getStartTime());
        newEvent.setEndTime(eventform.getEndTime());

        //Saving the event
        boolean saved = eventService.saveEvent(newEvent);
        String response = "Failed to save Event, Unknown Error";
        //Error handling (very basic)
        if (saved) {
            response = "Successfully saved event";
        }
        return ResponseEntity.
                created(URI.create("/events/"))
                .body(response);
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */

//    @GetMapping(value = "/usr/{id}", produces = "application/json")
//    public ResponseEntity<Event> findUserByID(@PathVariable Long id) {
//        boolean optionalDirector = eventService.findEventById(id);
//        return optionalDirector
//                .map(event -> ResponseEntity.ok(event))
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    /**
     * 2) Get all Patients
     * GET /patients
     */
    @GetMapping(produces = "application/json")
    public List<Event> getAllUsers() {
        return eventService.findAllEvents();
    }

    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    /**
     * 4) Delete a Patient
     * DELETE /patients/{id}
     */
//    @DeleteMapping("/usr/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUserById(id);
//        return ResponseEntity.noContent().build();
//    }

    /**
     * getAllActiveUsers()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllActiveUsers() {
//        return userService.findAllUsers();
//    }

    /**
     *  getAllNurses()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllNurses() {
//        return userService.findAllUsers();
//    }

    /**
     *  getAllDirectors()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllDirector() {
//        return userService.findAllUsers();
//    }

    /**
     *  getAllGuests()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllGuests() {
//        return userService.findAllUsers();
//    }


}
