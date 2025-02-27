package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import lombok.Data;

import java.util.List;

@Data
public class EventForm {

    private String name;
    private User director;
    private int startTime;
    private int endTime;
    private Status status;
    private List<Resource> resources;
    private List<User> nurses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getNurses() {
        return nurses;
    }

    public void setNurses(List<User> nurses) {
        this.nurses = nurses;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public User getDirector() {
        return director;
    }

    public void setDirector(User director) {
        this.director = director;
    }

    public EventForm() {
    }

}
