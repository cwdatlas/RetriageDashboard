package com.retriage.retriage.services;

import org.springframework.scheduling.annotation.Scheduled;

public interface EventTimerService {

    void updateEventDuration();

    void broadcastEventUpdates();
}
