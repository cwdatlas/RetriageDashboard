package com.retriage.retriage.models;

import com.retriage.retriage.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "event")
public class Event {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private User director;

    @Getter
    @Setter
    private int startTime;

    @Getter
    @Setter
    private int endTime;

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    @OneToMany(mappedBy = "parentEvent")
    private List<Resource> resources;

    @Getter
    @Setter
    @ManyToMany
    private List<User> nurses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getDirector() {
        return director;
    }

    public void setDirector(User director) {
        this.director = director;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<User> getNurses() {
        return nurses;
    }

    public void setNurses(List<User> nurses) {
        this.nurses = nurses;
    }

    public Event() {
    }
}
