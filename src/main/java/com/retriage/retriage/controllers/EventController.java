package com.retriage.retriage.controllers;

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
    public ResponseEntity<String> createEvent(@Valid @RequestBody EventForm eventform) {
        //Secondary validation...
        String response = "Failed to save Event, Unknown Error";
        User director = userService.getUserByEmail(eventform.getDirector().getEmail());
        if(director==null){
            response = "Failed to save Event, User not found";
        }else {

            //Creating the event
            Event newEvent = new Event();
            newEvent.setName(eventform.getName());
            newEvent.setDirector(director);
            newEvent.setNurses(eventform.getNurses());
            newEvent.setResources(eventform.getResources());
            newEvent.setStatus(eventform.getStatus());
            newEvent.setStartTime(eventform.getStartTime());
            newEvent.setEndTime(eventform.getEndTime());

            //Saving the event
            boolean saved = eventService.saveEvent(newEvent);
            //Error handling (very basic)
            if (saved) {
                response = "Successfully saved event";
            }
        }
        return ResponseEntity.
                created(URI.create("/events/"))
                .body(response);
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
