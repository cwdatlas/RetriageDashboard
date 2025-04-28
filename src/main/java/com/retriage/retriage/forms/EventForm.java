package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.PatientPool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Form object used for receiving event data from clients, typically for creating or updating an event.
 * Includes validation constraints to ensure data integrity upon submission.
 */
@Data
public class EventForm {

    /**
     * The unique identifier of the event. May be null for new events.
     */
    private Long id;

    /**
     * The name of the event. Must not be blank.
     */
    @NotBlank(message = "Event name is required")
    private String name;

    /**
     * A list of patient pools associated with the event.
     * Must not be null and must contain at least one pool.
     */
    @NotNull(message = "Must contain at least one PatientPool")
    @Size(min = 1, message = "Must contain at least one PatientPool")
    private List<PatientPool> pools;

    /**
     * The current status of the event (e.g., Running, Ended, Paused, Created). Must not be null.
     */
    @NotNull(message = "Status is required")
    private Status status;

    /**
     * The start time of the event, typically represented as a timestamp. Must not be null.
     */
    @NotNull(message = "Start time is required")
    private Long startTime;

    /**
     * The planned total duration of the event in milliseconds. Must not be null.
     */
    @NotNull(message = "End time is required")
    private Long duration;

    /**
     * The remaining duration of the event in milliseconds. Must not be null (at least 0).
     */
    @NotNull(message = "Duration Left must be not null. At least 0.")
    private long remainingDuration;

    /**
     * The timestamp when the event's status last changed. Must not be null (at least 0).
     */
    @NotNull(message = "timeOfStatusChange Left must be not null. At least 0.")
    private long timeOfStatusChange;

    /**
     * Default no-argument constructor.
     */
    public EventForm() {
    }
}