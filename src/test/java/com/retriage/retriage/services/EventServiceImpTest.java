package com.retriage.retriage.services;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.Resource;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        System.out.println("@BeforeEach - directorUser initialized: " + directorUser);

        // Create Nurse
        nurseUser = new User();
        nurseUser.setId(2L);
        nurseUser.setFirstName("Nurse");
        nurseUser.setLastName("Joy");
        nurseUser.setRole(Role.Nurse);
        System.out.println("@BeforeEach - nurseUser initialized: " + nurseUser);

        // Create a sample Resource
        Resource sampleResource = new Resource();
        sampleResource.setId(3L);
        sampleResource.setName("X-Ray Machine");
        System.out.println("@BeforeEach - sampleResource initialized: " + sampleResource);

        // Create Event
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setName("Test Event");
        sampleEvent.setDirector(directorUser);
        sampleEvent.setNurses(List.of(nurseUser));
        sampleEvent.setResources(List.of(sampleResource));
        sampleEvent.setStatus(Status.Setup);
        System.out.println("@BeforeEach - sampleEvent initialized: " + sampleEvent);
    }

    // Helper method to create a more complete sample Event
    private Event createSampleEvent(Long id, String name) {
        User director = new User();
        director.setId(4L);
        List<User> nurses = List.of(new User());
        List<Resource> resources = List.of(new Resource());
        Event event = new Event();
        event.setId(id);
        event.setName(name);
        event.setDirector(director);
        event.setNurses(nurses);
        event.setResources(resources);
        event.setStatus(Status.Setup);
        event.setStartTime(80); // Default start time
        event.setEndTime(95);   // Default end time
        return event;
    }

    // ==================== saveEvent TESTS ====================
    /**
     * Tests that saveEvent() returns true when a valid Event is provided.
     */
    @Test
    void saveEvent_ShouldReturnTrue_WhenValidEvent() {
        // Arrange
        when(eventRepository.save(sampleEvent)).thenReturn(sampleEvent); // Mock repository save

        // Act
        boolean result = eventServiceImp.saveEvent(sampleEvent);

        // Assert
        assertTrue(result, "saveEvent should return true for a valid event");
        verify(eventRepository, times(1)).save(sampleEvent);
    }

    /**
     * Tests that saveEvent() returns false when a null Event is provided.
     */
    @Test
    void saveEvent_ShouldReturnFalse_WhenNullEvent() {
        // Arrange
        Event nullEvent = null;

        // Act
        boolean result = eventServiceImp.saveEvent(nullEvent);

        // Assert
        assertFalse(result, "saveEvent should return false for a null event");
        verify(eventRepository, never()).save(any());
    }

    /**
     * Tests that saveEvent() throws IllegalArgumentException when Event name is null or empty.
     */
    @Test
    void saveEvent_ShouldThrowException_WhenInvalidName() {
        // Arrange
        Resource localSampleResource = new Resource();
        localSampleResource.setId(3L);
        localSampleResource.setName("X-Ray Machine");

        Event eventWithNullName = new Event();
        eventWithNullName.setDirector(directorUser);
        eventWithNullName.setNurses(List.of(nurseUser));
        eventWithNullName.setResources(List.of(localSampleResource)); // Using local resource
        eventWithNullName.setStatus(Status.Setup);

        Event eventWithEmptyName = new Event();
        eventWithEmptyName.setName("");
        eventWithEmptyName.setDirector(directorUser);
        eventWithEmptyName.setNurses(List.of(nurseUser)); // Using local nurse
        eventWithEmptyName.setResources(List.of(localSampleResource)); // Using local resource
        eventWithEmptyName.setStatus(Status.Setup);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithNullName),
                "saveEvent should throw exception for null name");
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithEmptyName),
                "saveEvent should throw exception for empty name");

        verify(eventRepository, never()).save(any());
    }

    /**
     * Tests that saveEvent() throws IllegalArgumentException when Director is null.
     */
    @Test
    void saveEvent_ShouldThrowException_WhenNullDirector() {
        // Arrange
        Resource localSampleResource = new Resource();
        localSampleResource.setId(3L);
        localSampleResource.setName("X-Ray Machine");

        Event eventWithNullDirector = new Event();
        eventWithNullDirector.setName("Test Event");
        eventWithNullDirector.setNurses(List.of(nurseUser)); // Using local nurse
        eventWithNullDirector.setResources(List.of(localSampleResource)); // Using local resource
        eventWithNullDirector.setStatus(Status.Setup);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithNullDirector),
                "saveEvent should throw exception for null director");

        verify(eventRepository, never()).save(any());
    }

    /**
     * Tests that saveEvent() throws IllegalArgumentException when Nurses list is null or empty.
     */
    @Test
    void saveEvent_ShouldThrowException_WhenInvalidNurses() {
        // Arrange
        Resource localSampleResource = new Resource();
        localSampleResource.setId(3L);
        localSampleResource.setName("X-Ray Machine");

        Event eventWithNullNurses = new Event();
        eventWithNullNurses.setName("Test Event");
        eventWithNullNurses.setDirector(directorUser);
        eventWithNullNurses.setResources(List.of(localSampleResource)); // Use local resource
        eventWithNullNurses.setStatus(Status.Setup);

        Event eventWithEmptyNurses = new Event();
        eventWithEmptyNurses.setName("Test Event");
        eventWithEmptyNurses.setDirector(directorUser);
        eventWithEmptyNurses.setNurses(List.of());
        eventWithEmptyNurses.setResources(List.of(localSampleResource)); // Use local resource
        eventWithEmptyNurses.setStatus(Status.Setup);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithNullNurses),
                "saveEvent should throw exception for null nurses list");
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithEmptyNurses),
                "saveEvent should throw exception for empty nurses list");

        verify(eventRepository, never()).save(any());
    }

    /**
     * Tests that saveEvent() throws IllegalArgumentException when Resources list is null or empty.
     */
    @Test
    void saveEvent_ShouldThrowException_WhenInvalidResources() {
        // Arrange
        Event eventWithNullResources = new Event();
        eventWithNullResources.setName("Test Event");
        eventWithNullResources.setDirector(directorUser);
        eventWithNullResources.setNurses(List.of(nurseUser));
        eventWithNullResources.setStatus(Status.Setup);

        Event eventWithEmptyResources = new Event();
        eventWithEmptyResources.setName("Test Event");
        eventWithEmptyResources.setDirector(directorUser);
        eventWithEmptyResources.setNurses(List.of(nurseUser));
        eventWithEmptyResources.setResources(List.of());
        eventWithEmptyResources.setStatus(Status.Setup);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithNullResources),
                "saveEvent should throw exception for null resources list");
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithEmptyResources),
                "saveEvent should throw exception for empty resources list");

        verify(eventRepository, never()).save(any());
    }

    /**
     * Tests that saveEvent() throws IllegalArgumentException when Status is null.
     */
    @Test
    void saveEvent_ShouldThrowException_WhenNullStatus() {
        // Arrange
        Resource localSampleResource = new Resource();
        localSampleResource.setId(3L);
        localSampleResource.setName("X-Ray Machine");

        Event eventWithNullStatus = new Event();
        eventWithNullStatus.setName("Test Event");
        eventWithNullStatus.setDirector(directorUser); // Using director from setUp
        eventWithNullStatus.setNurses(List.of(nurseUser));
        eventWithNullStatus.setResources(List.of(localSampleResource)); // Using local resource
        // Status is intentionally left as null

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(eventWithNullStatus),
                "saveEvent should throw exception for null status");

        verify(eventRepository, never()).save(any());
    }

    // ==================== saveEvent EDGE CASE TESTS ====================
    /**
     * Edge Case Test: Tests saving an event that already has an ID set.
     * Behavior: Should still attempt to save (potentially update if ID exists in DB, or insert) without error.
     */
    @Test
    void saveEvent_ResourceWithExistingId_ShouldSaveWithoutError() {
        // Arrange
        Event eventToSave = createSampleEvent(10L, "ExistingIDEvent");
        when(eventRepository.save(eventToSave)).thenReturn(eventToSave);

        // Act
        boolean result = eventServiceImp.saveEvent(eventToSave);

        // Assert
        assertTrue(result, "saveEvent should return true when saving with existing ID");
        verify(eventRepository, times(1)).save(eventToSave);
    }

    /**
     * Edge Case Test: Tests saving an event where startTime is after endTime.
     * Behavior: Should throw IllegalArgumentException due to validation.
     */
    @Test
    void saveEvent_StartTimeAfterEndTime_ShouldThrowException() {
        // Arrange
        Event invalidTimeEvent = createSampleEvent(null, "Invalid Time Event");
        invalidTimeEvent.setStartTime(100);
        invalidTimeEvent.setEndTime(90);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.saveEvent(invalidTimeEvent),
                "saveEvent should throw exception when startTime is after endTime");
        verify(eventRepository, never()).save(any());
    }


    // ==================== findEventById TESTS ====================
    /**
     * Tests that findEventById() returns the correct Event when it exists.
     */
    @Test
    void findEventById_ShouldReturnEvent_WhenFound() {
        // Arrange
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(sampleEvent));

        // Act
        Event foundEvent = eventServiceImp.findEventById(eventId);

        // Assert
        assertNotNull(foundEvent, "Found event should not be null");
        assertEquals(sampleEvent.getId(), foundEvent.getId(), "Found event ID should match");
        assertEquals(sampleEvent.getName(), foundEvent.getName(), "Found event name should match");
        assertEquals(sampleEvent.getDirector(), foundEvent.getDirector(), "Found event director should match");
        assertEquals(sampleEvent.getNurses(), foundEvent.getNurses(), "Found event nurses should match");
        assertEquals(sampleEvent.getResources(), foundEvent.getResources(), "Found event resources should match");
        assertEquals(sampleEvent.getStatus(), foundEvent.getStatus(), "Found event status should match");
        verify(eventRepository, times(1)).findById(eventId);
    }

    /**
     * Tests that findEventById() returns null when the Event is not found.
     */
    @Test
    void findEventById_ShouldReturnNull_WhenNotFound() {
        // Arrange
        Long nonExistentEventId = 999L;
        when(eventRepository.findById(nonExistentEventId)).thenReturn(java.util.Optional.empty());

        // Act
        Event foundEvent = eventServiceImp.findEventById(nonExistentEventId);

        // Assert
        assertNull(foundEvent, "Found event should be null when not found");
        verify(eventRepository, times(1)).findById(nonExistentEventId);
    }

    /**
     * Tests that findEventById() returns null when a null ID is provided.
     */
    @Test
    void findEventById_ShouldReturnNull_WhenNullId() {
        // Arrange
        Long nullEventId = null;
        when(eventRepository.findById(nullEventId)).thenReturn(java.util.Optional.empty()); // Mock repository for null ID

        // Act
        Event foundEvent = eventServiceImp.findEventById(nullEventId);

        // Assert
        assertNull(foundEvent, "Found event should be null when a null ID is provided");
        verify(eventRepository, times(1)).findById(nullEventId);
    }

    // ==================== findEventById EDGE CASE TESTS ====================
    /**
     * Edge Case Test: Tests findEventById() with a negative ID.
     * Behavior: Should return null as no event with a negative ID should exist.
     */
    @Test
    void findEventById_ShouldReturnNull_WhenNegativeId() {
        // Arrange
        Long negativeEventId = -5L;
        when(eventRepository.findById(negativeEventId)).thenReturn(java.util.Optional.empty());

        // Act
        Event foundEvent = eventServiceImp.findEventById(negativeEventId);

        // Assert
        assertNull(foundEvent, "Found event should be null for a negative ID");
        verify(eventRepository, times(1)).findById(negativeEventId);
    }

    // ==================== UpdateEvent TESTS ====================
    /**
     * Tests that UpdateEvent() successfully updates an existing event.
     */
    @Test
    void UpdateEvent_ShouldReturnUpdatedEvent_WhenEventExistsAndDataIsValid() {
        // Arrange
        Long eventId = 1L;
        Event existingEvent = createSampleEvent(eventId, "Original Event");
        Event updatedEventData = createSampleEvent(eventId, "Updated Event");

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.save(updatedEventData)).thenReturn(updatedEventData);

        // Act
        Event result = eventServiceImp.UpdateEvent(eventId, updatedEventData);

        // Assert
        assertNotNull(result, "Updated event should not be null");
        assertEquals(eventId, result.getId(), "Updated event ID should match");
        assertEquals("Updated Event", result.getName(), "Updated event name should match");
        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, times(1)).save(updatedEventData);
    }

    /**
     * Tests that UpdateEvent() returns null when the event to update does not exist.
     */
    @Test
    void UpdateEvent_ShouldReturnNull_WhenEventDoesNotExist() {
        // Arrange
        Long nonExistentEventId = 999L;
        Event updatedEventData = createSampleEvent(nonExistentEventId, "Updated Event");

        when(eventRepository.existsById(nonExistentEventId)).thenReturn(false);

        // Act
        Event result = eventServiceImp.UpdateEvent(nonExistentEventId, updatedEventData);

        // Assert
        assertNull(result, "Should return null when event does not exist");
        verify(eventRepository, times(1)).existsById(nonExistentEventId);
        verify(eventRepository, never()).save(any());
    }

    /**
     * Tests that UpdateEvent() throws IllegalArgumentException when the updated Event data is invalid (e.g., null name).
     */
    @Test
    void UpdateEvent_ShouldThrowException_WhenUpdatedDataIsInvalid() {
        // Arrange
        Long eventId = 1L;
        Event invalidUpdatedEventData = createSampleEvent(eventId, null); // Event with null name

        when(eventRepository.existsById(eventId)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.UpdateEvent(eventId, invalidUpdatedEventData),
                "Should throw IllegalArgumentException for invalid updated data");

        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, never()).save(any());
    }

    // ==================== UpdateEvent EDGE CASE TESTS ====================
    /**
     * Edge Case Test: Tests updating an event where startTime is after endTime.
     * Behavior: Should throw IllegalArgumentException due to validation.
     */
    @Test
    void UpdateEvent_StartTimeAfterEndTime_ShouldThrowException() {
        // Arrange
        Long eventIdToUpdate = 20L;
        Event invalidTimeEvent = createSampleEvent(eventIdToUpdate, "Updated Event with Invalid Time");
        invalidTimeEvent.setStartTime(150);
        invalidTimeEvent.setEndTime(120);

        when(eventRepository.existsById(eventIdToUpdate)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventServiceImp.UpdateEvent(eventIdToUpdate, invalidTimeEvent),
                "UpdateEvent should throw exception when startTime is after endTime");
        verify(eventRepository, times(1)).existsById(eventIdToUpdate);
        verify(eventRepository, never()).save(any());
    }

    // ==================== findAllEvents TESTS ====================
    /**
     * Tests that findAllEvents() returns an empty list when no events exist in the repository.
     */
    @Test
    void findAllEvents_ShouldReturnEmptyList_WhenNoEventsExist() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        // Act
        List<Event> allEvents = eventServiceImp.findAllEvents();

        // Assert
        assertNotNull(allEvents, "The list of events should not be null");
        assertTrue(allEvents.isEmpty(), "The list of events should be empty when no events exist");
        verify(eventRepository, times(1)).findAll();
    }

    /**
     * Tests that findAllEvents() returns a list of all events when events exist in the repository.
     */
    @Test
    void findAllEvents_ShouldReturnListOfEvents_WhenEventsExist() {
        // Arrange
        Event event1 = createSampleEvent(1L, "Event One");
        Event event2 = createSampleEvent(2L, "Event Two");
        List<Event> mockEvents = List.of(event1, event2);
        when(eventRepository.findAll()).thenReturn(mockEvents);

        // Act
        List<Event> allEvents = eventServiceImp.findAllEvents();

        // Assert
        assertNotNull(allEvents, "The list of events should not be null");
        assertEquals(2, allEvents.size(), "The list should contain the expected number of events");
        assertTrue(allEvents.contains(event1), "The list should contain Event One");
        assertTrue(allEvents.contains(event2), "The list should contain Event Two");
        verify(eventRepository, times(1)).findAll();
    }

    // ==================== deleteEventById TESTS ====================
    /**
     * Tests that deleteEventById() succeeds when the event exists.
     */
    @Test
    void deleteEventById_ShouldSucceed_WhenEventExists() {
        // Arrange
        Long eventIdToDelete = 5L;
        when(eventRepository.existsById(eventIdToDelete)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(eventIdToDelete);

        // Act
        eventServiceImp.deleteEventById(eventIdToDelete);

        // Assert
        verify(eventRepository, times(1)).existsById(eventIdToDelete);
        verify(eventRepository, times(1)).deleteById(eventIdToDelete);
    }

    /**
     * Tests that deleteEventById() does nothing when the event does not exist.
     */
    @Test
    void deleteEventById_ShouldDoNothing_WhenEventDoesNotExist() {
        // Arrange
        Long nonExistentEventId = 999L;
        when(eventRepository.existsById(nonExistentEventId)).thenReturn(false);

        // Act
        eventServiceImp.deleteEventById(nonExistentEventId);

        // Assert
        verify(eventRepository, times(1)).existsById(nonExistentEventId);
        verify(eventRepository, never()).deleteById(any());
    }

    // ==================== deleteEventById EDGE CASE TESTS ====================
    /**
     * Edge Case Test: Tests deleting an event with a negative ID.
     * Behavior: Should check if it exists (likely false) and not attempt to delete.
     */
    @Test
    void deleteEventById_ShouldHandleNegativeId() {
        // Arrange
        Long negativeEventIdToDelete = -10L;
        when(eventRepository.existsById(negativeEventIdToDelete)).thenReturn(false);

        // Act
        eventServiceImp.deleteEventById(negativeEventIdToDelete);

        // Assert
        verify(eventRepository, times(1)).existsById(negativeEventIdToDelete);
        verify(eventRepository, never()).deleteById(any());
    }
}


