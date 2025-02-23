package com.retriage.retriage.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Entity
@Table(name = "resources")
public class Resource {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int processTime;
    @Getter
    @Setter
    private boolean active;
    @Getter
    @Setter
    private boolean useable;
    @Getter
    @Setter
    @ManyToMany
    private List<Patient> patients;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event parentEvent;

    public Resource() {

    }
}
