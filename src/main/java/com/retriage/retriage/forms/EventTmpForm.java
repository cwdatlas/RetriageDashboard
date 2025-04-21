package com.retriage.retriage.forms;

import com.retriage.retriage.models.PatientPoolTmp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Form object used for receiving data to create a new {@link com.retriage.retriage.models.Event}.
 * This form is specifically designed for creating an event based on a list of {@link PatientPoolTmp}s (patient pool templates).
 * Includes validation constraints to ensure data integrity upon submission.
 */
@Data
public class EventTmpForm {

    /**
     * The name of the event to be created. Must not be blank.
     */
    @NotBlank(message = "Event name is required")
    private String name;

    /**
     * The planned total duration of the event in milliseconds. Must not be null.
     */
    @NotNull(message = "End time is required")
    private Long duration;

    /**
     * A list of patient pool templates to use when creating the event's pools.
     * Must not be null and must contain at least one template.
     */
    @NotNull(message = "Must contain at least one PatientPool Template")
    @Size(min = 1, message = "Must contain at least one PatientPool Template")
    private List<PatientPoolTmp> poolTmps;

    /**
     * Default no-argument constructor.
     */
    public EventTmpForm() {
    }
}