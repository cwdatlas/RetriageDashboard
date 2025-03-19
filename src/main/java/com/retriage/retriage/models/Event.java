package com.retriage.retriage.models;

import com.retriage.retriage.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "events") // Renamed to avoid MySQL 'event' keyword conflicts
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //Owner of director
    @ManyToOne
    private User director;

    private Long startTime;

    private Long duration;

    private Status status;

    //Not Owner
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private List<PatientPool> pools;

    //Owner
    @ManyToMany
    private List<User> nurses;

    public Event() {
    }
}
