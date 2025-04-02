package com.retriage.retriage.services;

import org.springframework.scheduling.annotation.Scheduled;

public interface EventTimerService {

    @Scheduled(fixedRate = 1000)
    void updateEventDuration();

    @Scheduled(fixedRate = 30000)
    void broadcastEventUpdates();
}
