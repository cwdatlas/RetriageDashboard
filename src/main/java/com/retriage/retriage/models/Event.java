package com.retriage.retriage.models;

import jakarta.persistence.*;

@Entity
@Table(name = "event")
public class Event {
    //TODO update model to match User from the database model
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int DirectorId;
    private String resources; //TODO This should be a pointer to a different table, a key.

    public Event() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDirectorId() {
        return DirectorId;
    }

    public void setDirectorId(int directorId) {
        DirectorId = directorId;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }
}
