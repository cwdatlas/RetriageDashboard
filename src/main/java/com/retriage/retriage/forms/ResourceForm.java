package com.retriage.retriage.forms;

import com.retriage.retriage.models.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ResourceForm {
    @Getter
    @Setter
    @NotBlank(message = "PatientPool name is required")
    private String name;
    @Getter
    @Setter
    @Positive(message = "Process time must be a positive number")
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

    public ResourceForm() {
    }
}
