package com.retriage.retriage.enums;

/**
 * Represents the possible statuses of an event within the system.
 */
public enum Status {
    /**
     * Indicates that the event is currently active and in progress.
     */
    Running,
    /**
     * Indicates that the event has concluded or finished.
     */
    Ended,
    /**
     * Indicates that the event is temporarily halted.
     */
    Paused,
    /**
     * Indicates that the event has been created but has not yet started.
     */
    Created,
}