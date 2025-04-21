package com.retriage.retriage.models;

import com.retriage.retriage.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Represents an event, such as a simulation exercise or real-world incident.
 * This class is a JPA entity mapped to the "events" table in the database.
 * It holds information about the event's state, timing, and associated patient pools.
 */
@Data
@Entity
@Table(name = "events") // Renamed to avoid MySQL 'event' keyword conflicts
public class Event {

    /**
     * The unique identifier for the event. This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the event.
     */
    private String name;

    /**
     * The start time of the event, typically stored as a timestamp (milliseconds since epoch).
     */
    private Long startTime;

    /**
     * The planned total duration of the event in milliseconds.
     */
    private Long duration;

    /**
     * The current status of the event (e.g., Running, Ended, Paused, Created).
     * See {@link Status} enum.
     */
    private Status status;

    /**
     * The list of patient pools associated with this event.
     * This is a one-to-many relationship, and operations on the Event will cascade to PatientPools.
     * The foreign key is managed by the PatientPool entity via the 'event_id' column.
     */
    //Not Owner
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private List<PatientPool> pools;

    /**
     * The remaining duration of the event in milliseconds. Must not be null (at least 0).
     * This field is likely updated during event execution.
     */
    @NotNull(message = "Duration Left must be not null. At least 0.")
    private long remainingDuration;

    /**
     * The timestamp (milliseconds since epoch) when the event's status last changed. Must not be null (at least 0).
     */
    @NotNull(message = "timeOfStatusChange Left must be not null. At least 0.")
    private long timeOfStatusChange;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Event() {
    }
}