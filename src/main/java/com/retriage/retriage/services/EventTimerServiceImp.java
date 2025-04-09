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

@Service
public class EventTimerServiceImp implements EventTimerService {
    private static final Logger logger = LoggerFactory.getLogger(EventTimerServiceImp.class);
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;


    EventTimerServiceImp(EventService eventService, SimpMessagingTemplate messagingTemplate) {
        this.eventService = eventService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void updateEventDuration() {
        Event activeEvent = eventService.findActiveEvent();
        boolean updateEvent = false;
        if (activeEvent != null) {
            logger.debug("updateEventDuration: activeEvent {} is currently active with {} seconds remaining.", activeEvent.getName(), activeEvent.getRemainingDuration() / 6000);
            long now = System.currentTimeMillis();
            long startTime = activeEvent.getTimeOfStatusChange();
            long duration = activeEvent.getRemainingDuration(); // original duration in milliseconds
            long elapsed = now - startTime;
            long remaining = duration - elapsed;
            if (remaining < 0) {
                activeEvent.setRemainingDuration(0);
                activeEvent.setStatus(Status.Ended);
            } else {
                // Checking if there is a patient that has spent enough time in the queue to be processed.
                for (PatientPool pool : activeEvent.getPools()) {
                    if (pool.getPoolType() == PoolType.MedService && !pool.getPatients().isEmpty()) {
                        long poolElapsed = now - pool.getStartedProcessingAt();
                        Patient patient = pool.getPatients().getFirst();
                        long processedTime = pool.getProcessTime();
                        if (poolElapsed > processedTime && !patient.isProcessed()) {
                            patient.setProcessed(true);
                            updateEvent = true;
                            if (pool.isAutoDischarge()) {
                                pool.getPatients().remove(patient);
                                pool.setStartedProcessingAt(System.currentTimeMillis());
                            }
                        }
                    }
                }
            }

            if (activeEvent.getStatus() == Status.Ended || updateEvent) {
                eventService.updateEvent(activeEvent.getId(), activeEvent);
                broadcastEventUpdates();
            }
        }
    }

    @Override
    @Transactional
    public void broadcastEventUpdates() {
        Event activeEvent = eventService.findActiveEvent();
        if (activeEvent == null) {
            messagingTemplate.convertAndSend("/topic/event_updates",
                    new ResponseWrapper<Event>(HttpStatus.NOT_FOUND.value(), "There is not an event running currently.",null ));
        } else {
            messagingTemplate.convertAndSend("/topic/event_updates",
                    new ResponseWrapper<Event>(HttpStatus.OK.value(), "Nominal Event Update", activeEvent));
        }
    }
}
