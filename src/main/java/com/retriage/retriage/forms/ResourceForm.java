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

    public ResourceForm() {
    }
}
