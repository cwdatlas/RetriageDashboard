package com.retriage.retriage.models;

import com.retriage.retriage.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event")
public class Event {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int DirectorId;
    @Getter
    @Setter
    private int startTime;
    @Getter
    @Setter
    private int endTime;
    @Getter
    @Setter
    private Status status;

    public Event() {
    }

    public Event(String name, int directorId, int startTime, int endTime, Status status) {
        this.name = name;
        DirectorId = directorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
