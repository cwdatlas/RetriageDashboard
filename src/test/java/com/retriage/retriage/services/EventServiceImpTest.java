package com.retriage.retriage.services;

import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.User;
import com.retriage.retriage.enums.Role;
import com.retriage.retriage.repositories.EventRepository;
import com.retriage.retriage.services.EventServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EventServiceImpTest {

    @Autowired
    private EventServiceImp eventServiceImp;

    @MockitoBean
    private EventRepository eventRepository;

    private Event sampleEvent;
    private User directorUser;
    private User nurseUser;

    @BeforeEach
    void setUp() {
        // Create Director
        directorUser = new User();
        directorUser.setId(1L);
        directorUser.setFirstName("Dr.");
        directorUser.setLastName("Pepper");
        directorUser.setRole(Role.Director);

        // Create Nurse
        nurseUser = new User();
        nurseUser.setId(2L);
        directorUser.setFirstName("Nurse");
        directorUser.setLastName("Joy");
        directorUser.setRole(Role.Nurse);

        // Create Event
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setName("Test Event");
        sampleEvent.setDirector(directorUser);
        sampleEvent.setNurses(List.of(nurseUser));

    }

    @Test
    void saveEvent_ShouldReturnTrue_WhenValidEvent() {
        boolean result = eventServiceImp.saveEvent(sampleEvent);
        assertTrue(result, "Event should be saved successfully");
    }
}
