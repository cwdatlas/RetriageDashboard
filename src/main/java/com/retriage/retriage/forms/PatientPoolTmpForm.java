package com.retriage.retriage.forms;

import com.retriage.retriage.enums.PoolType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Form object used for receiving data to create a new {@link com.retriage.retriage.models.PatientPoolTmp}.
 * This form represents the template data for creating patient pools within an event.
 * Includes validation constraints to ensure data integrity upon submission.
 */
@Data
public class PatientPoolTmpForm {

    /**
     * The name for the patient pool template. Must not be blank.
     */
    @NotBlank(message = "PatientPool name is required")
    private String name;

    /**
     * The default process time (duration a patient typically spends in this pool) in milliseconds. Must not be null.
     */
    @NotNull
    private Long processTime;

    /**
     * Flag indicating whether patients in pools created from this template should be automatically discharged. Must not be null.
     * Note: {@code @NotBlank} is not applicable to boolean primitives; {@code @NotNull} is sufficient for validation.
     */
    @NotNull(message = "Must include true or false for autoDischarge attribute")
    private boolean autoDischarge;

    /**
     * The type of patient pool this template represents (e.g., Bay, MedService, Floor). Must not be null.
     * See {@link PoolType} for possible values.
     */
    @NotNull(message = "Must set poolType to either 'Bay' or 'MedicalService'")
    private PoolType poolType;

    /**
     * The number of individual patient pools to create from this template during event setup.
     * Must be at least 1. {@code @NotNull} is redundant for primitive int.
     */
    @NotNull(message = "Must pass a value of more than 0 into poolNumber") // Message suggests > 0, enforced by @Min(1)
    @Min(1)
    private int poolNumber;

    /**
     * The maximum number of patients that can be in the queue for pools created from this template.
     * Must be at least 0. {@code @NotNull} is redundant for primitive int.
     */
    @NotNull // Redundant for primitive int
    @Min(0)
    private int queueSize;

    /**
     * An optional string representing an icon associated with this pool type.
     */
    private String icon;

    /**
     * Default no-argument constructor.
     */
    public PatientPoolTmpForm() {
    }
}