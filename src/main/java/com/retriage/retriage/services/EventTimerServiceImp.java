package com.retriage.retriage.services;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Event;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class EventTimerServiceImp implements EventTimerService {
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;

    EventTimerServiceImp(EventService eventService, SimpMessagingTemplate messagingTemplate){
        this.eventService = eventService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void updateEventDuration() {
        Event activeEvent = eventService.findActiveEvent();
        if (activeEvent != null && activeEvent.getStatus() == Status.Running) {
            long now = System.currentTimeMillis();
            long startTime = activeEvent.getStartTime();
            long duration = activeEvent.getDuration(); // original duration in milliseconds
            long elapsed = now - startTime;
            long remaining = duration - elapsed;
            if (remaining < 0) {
                remaining = 0;
            }
            // You can store remaining duration in a dedicated field, e.g. setRemainingDuration(remaining)
            activeEvent.setRemainingDuration(remaining);
            eventService.updateEvent(activeEvent.getId(), activeEvent);
        }
    }

    @Override
    public void broadcastEventUpdates() {
        Event activeEvent = eventService.findActiveEvent();
        if (activeEvent != null) {
            // Broadcast updated event to clients via WebSocket
            messagingTemplate.convertAndSend("/topic/event_updates", activeEvent);
        }
    }
}
