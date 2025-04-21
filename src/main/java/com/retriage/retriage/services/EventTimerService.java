package com.retriage.retriage.services;

/**
 * Service interface defining the contract for managing event timing and broadcasting updates.
 * Implementations of this interface are typically responsible for tasks like
 * counting down event duration and sending real-time updates to connected clients.
 */
public interface EventTimerService {

    /**
     * Updates the remaining duration of the currently active event.
     * This method is likely called periodically by a scheduled task.
     */
    void updateEventDuration();

    /**
     * Broadcasts the current state of the active event to all subscribed clients.
     * This method is likely called periodically, potentially after the duration is updated.
     */
    void broadcastEventUpdates();
}