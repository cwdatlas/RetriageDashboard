package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.PatientPool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class EventForm {

    private Long id;

    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull(message = "Must contain at least one PatientPool")
    @Size(min = 1, message = "Must contain at least one PatientPool")
    private List<PatientPool> pools;

    @NotNull(message = "Status is required")
    private Status status;

    @NotNull(message = "Start time is required")
    private Long startTime;

    @NotNull(message = "End time is required")
    private Long duration;

    @NotNull(message = "Duration Left must be not null. At least 0.")
    private long remainingDuration;

    @NotNull(message = "timeOfStatusChange Left must be not null. At least 0.")
    private long timeOfStatusChange;

    public EventForm() {
    }

}