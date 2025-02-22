package com.retriage.retriage.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resources")
public class Resource {
//TODO update model to match User from the database model

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

    public Resource() {

    }

    public Resource(String name, int processTime, boolean active, boolean useable) {
        this.name = name;
        this.processTime = processTime;
        this.active = active;
        this.useable = useable;
    }
}
