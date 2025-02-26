package com.retriage.retriage.forms;

import com.retriage.retriage.models.Event;
import com.retriage.retriage.models.Patient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ResourceForm {
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
    private List<Patient> patients;
    @Getter
    @Setter
    private Event parentEvent;

    public Event getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(Event parentEvent) {
        this.parentEvent = parentEvent;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public boolean isUseable() {
        return useable;
    }

    public void setUseable(boolean useable) {
        this.useable = useable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceForm() {
    }
}
