package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.PoolType;
import com.retriage.retriage.enums.Role;
import com.retriage.retriage.enums.Status;
import com.retriage.retriage.forms.EventTmpForm;
import com.retriage.retriage.models.*;
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
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventTmpForm eventform) {
        //Secondary validation...
        List<String> errorList = new ArrayList<>();
        // Validate Director
        if (eventform.getDirector() == null) {
            errorList.add("Director must  be added to event");
        } else if (eventform.getDirector().getEmail() == null) {
            errorList.add("Submitted director lacking email address");
        } else {
            User director = userService.getUserByEmail(eventform.getDirector().getEmail());
            if (director == null){
                errorList.add("Director does not exist, not authorized to create an event");
            } else if(director.getRole() != Role.Director) {
                errorList.add("User " + director.getEmail() + " is not a director, they are a " + director.getRole());
            }
        }
        // Patient Pool Template validation
        List<PatientPool> pools = new ArrayList<>();
        if(eventform.getPoolTmps().isEmpty()){
            errorList.add("Must add at least 1 Pool Template");
        }else{
            PatientPoolTmp[] templates = eventform.getPoolTmps().toArray(new PatientPoolTmp[eventform.getPoolTmps().size()]);
            for(PatientPoolTmp poolTmp : templates){
                for(int i = 1; i <= poolTmp.getPoolNumber(); i++){
                    PatientPool patientPool = new PatientPool();
                    patientPool.setPoolType(poolTmp.getPoolType());
                    patientPool.setUseable(poolTmp.isUseable());
                    if(poolTmp.getPoolType() == PoolType.Bay){
                        patientPool.setProcessTime(eventform.getEndTime()); //TODO Keep process time and end time in the same type of time
                    }else{
                        patientPool.setProcessTime(poolTmp.getProcessTime());
                    }
                    // Create new name based on template name
                    if(poolTmp.getPoolNumber() == 1){
                        patientPool.setName(poolTmp.getName());
                    }else{
                        patientPool.setName(poolTmp.getName() + " " + i);
                    }
                    patientPool.setPatients(new ArrayList<Patient>());
                    patientPool.setActive(true);
                    pools.add(patientPool);
                }
            }
        }
        // validation for event
        if(pools.isEmpty()) {
            errorList.add("Must add at least 1 Pool Template"); //TODO use dry practices to exclude this piece of code
        }else if (errorList.isEmpty()) {
            Event newEvent = new Event();
            newEvent.setName(eventform.getName());
            User director = userService.getUserByEmail(eventform.getDirector().getEmail());
            newEvent.setDirector(director);
            newEvent.setNurses(new ArrayList<User>());
            newEvent.setPools(pools);
            newEvent.setStatus(Status.Paused);
            newEvent.setStartTime(0);
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
