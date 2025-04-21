package com.retriage.retriage.services;

import com.retriage.retriage.enums.PoolType;
import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.models.ResponseWrapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link EventTimerService} interface.
 * This service manages the timing of the active event and broadcasts its state
 * using scheduled tasks and WebSocket messaging.
 */
@Service
public class EventTimerServiceImp implements EventTimerService {
    /**
     * Logger for this service implementation.
     */
    private static final Logger logger = LoggerFactory.getLogger(EventTimerServiceImp.class);
    /**
     * Service for managing event business logic and data access.
     */
    private final EventService eventService;
    /**
     * Spring component for sending messages to WebSocket destinations.
     */
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * Constructs an instance of {@code EventTimerServiceImp}.
     *
     * @param eventService      The {@link EventService} used for event data operations.
     * @param messagingTemplate The {@link SimpMessagingTemplate} used for sending WebSocket messages.
     */
    EventTimerServiceImp(EventService eventService, SimpMessagingTemplate messagingTemplate) {
        this.eventService = eventService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Periodically updates the remaining duration of the currently active event.
     * This method is scheduled to run at a fixed rate (currently every 1000ms).
     * It finds the active event, calculates the elapsed time, updates the remaining duration,
     * and handles the transition to {@link Status#Ended} if the duration reaches zero.
     * It also checks if patients in Medical Service pools have completed processing.
     * Updates are persisted and broadcast if the status changes or a patient is processed.
     */
    @Override
    @Scheduled(fixedRate = 1000) // Configured to run every 1000 milliseconds (1 second)
    @Transactional // Ensures database operations within this method are atomic
    public void updateEventDuration() {
        Event activeEvent = eventService.findActiveEvent();
        boolean updateEvent = false; // Flag to indicate if the event needs saving and broadcasting
        if (activeEvent != null) {
            logger.debug("updateEventDuration: activeEvent {} is currently active with {} seconds remaining.", activeEvent.getName(), activeEvent.getRemainingDuration() / 6000);
            long now = System.currentTimeMillis();
            long startTime = activeEvent.getTimeOfStatusChange(); // Timestamp of the last status change (e.g., when it started Running)
            long duration = activeEvent.getRemainingDuration(); // remaining duration from the start time of the current status
            long elapsed = now - startTime;
            long remaining = activeEvent.getDuration() - (now - activeEvent.getStartTime());
            activeEvent.setRemainingDuration(remaining);

            if (remaining < 0) {
                activeEvent.setRemainingDuration(0);
                activeEvent.setStatus(Status.Ended);
                updateEvent = true; // Status changed, need to save and broadcast
            }

            // Checking patient processing completion in MedService pools
            for (PatientPool pool : activeEvent.getPools()) {
                if (pool.getPoolType() == PoolType.MedService && !pool.getPatients().isEmpty()) {
                    Patient patient = pool.getPatients().getFirst(); // Assuming only the first patient in the list is actively being processed
                    long poolElapsed = now - pool.getStartedProcessingAt(); // Elapsed time since this patient started processing in the pool
                    long processedTime = pool.getProcessTime(); // Required processing time for this pool type

                    if (poolElapsed >= processedTime && !patient.isProcessed()) {
                        patient.setProcessed(true);
                        updateEvent = true; // Patient status changed, need to save and broadcast
                        if (pool.isAutoDischarge()) {
                            pool.getPatients().removeFirst(); // Remove the first patient
                            pool.setStartedProcessingAt(System.currentTimeMillis()); // Reset timer for the next patient if any
                            logger.debug("updateEventDuration: Patient in pool {} auto-discharged.", pool.getName());
                        }
                        logger.debug("updateEventDuration: Patient in pool {} marked as processed.", pool.getName());
                    }
                }
            }


            // Save the event if its status changed or if any patient processing/discharge occurred
            if (activeEvent.getStatus() == Status.Ended || updateEvent) {
                eventService.updateEvent(activeEvent.getId(), activeEvent);
                // Only broadcast if there was a significant update
                broadcastEventUpdates();
            } else {
                // Broadcast even if only duration changes, to update the timer on the frontend
                broadcastEventUpdates();
            }
        }
    }

    /**
     * Broadcasts the current state of the active event to the WebSocket topic {@code /topic/event_updates}.
     * Sends the full {@link Event} object if an active event is found, wrapped in a {@link ResponseWrapper}.
     * If no active event is found, sends a "not found" message.
     */
    @Override
    @Transactional // Ensures consistency with the active event state before broadcasting
    public void broadcastEventUpdates() {
        Event activeEvent = eventService.findActiveEvent();
        if (activeEvent == null) {
            logger.debug("broadcastEventUpdates: No active event to broadcast.");
            messagingTemplate.convertAndSend("/topic/event_updates",
                    new ResponseWrapper<Event>(HttpStatus.NOT_FOUND.value(), "There is not an event running currently.", null));
        } else {
            logger.debug("broadcastEventUpdates: Broadcasting active event update for event ID {}", activeEvent.getId());
            messagingTemplate.convertAndSend("/topic/event_updates",
                    new ResponseWrapper<Event>(HttpStatus.OK.value(), "Nominal Event Update", activeEvent));
        }
    }
}