package com.retriage.retriage.services;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EventTimerServiceImp implements EventTimerService {
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;

    EventTimerServiceImp(EventService eventService, SimpMessagingTemplate messagingTemplate){
        this.eventService = eventService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void updateEventDuration() {
        Event activeEvent = eventService.findActiveEvent();
        if (activeEvent != null) {
            long now = System.currentTimeMillis();
            long startTime = activeEvent.getStartTime();
            long duration = activeEvent.getDuration(); // original duration in milliseconds
            long elapsed = now - startTime;
            long remaining = duration - elapsed;
            if (remaining < 0) {
                remaining = 0;
                activeEvent.setStatus(Status.Ended);
            }
            // You can store remaining duration in a dedicated field, e.g. setRemainingDuration(remaining)
            activeEvent.setRemainingDuration(remaining);
            eventService.updateEvent(activeEvent.getId(), activeEvent);
            if(activeEvent.getStatus() == Status.Ended || activeEvent.getRemainingDuration() % 30 == 0){
                broadcastEventUpdates();
            }
        }
    }

    @Override
    @Transactional
    public void broadcastEventUpdates() {
        Event activeEvent = eventService.findActiveEvent();
        if (activeEvent == null) {
            Event errorReturnEvent = new Event();
            errorReturnEvent.setName("NoEventFound");
            messagingTemplate.convertAndSend("/topic/event_updates", errorReturnEvent);
        }else{
            messagingTemplate.convertAndSend("/topic/event_updates", activeEvent);;
        }
    }
}
