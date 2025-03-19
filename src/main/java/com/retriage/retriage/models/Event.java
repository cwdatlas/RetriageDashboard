package com.retriage.retriage.models;

import com.retriage.retriage.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "events") // Renamed to avoid MySQL 'event' keyword conflicts
public class Event {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;

    //Owner of director
    @Getter
    @Setter
    @ManyToOne
    private User director;

    @Getter
    @Setter
    private int startTime;

    @Getter
    @Setter
    private int duration;

    @Getter
    @Setter
    private Status status;

    //Not Owner
    @Getter
    @Setter
    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    private List<PatientPool> resources;

    //Owner
    @Getter
    @Setter
    @ManyToMany
    private List<User> nurses;

    public Event() {
    }
}
