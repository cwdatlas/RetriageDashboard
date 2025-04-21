package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Condition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Form object used for receiving patient data from clients, typically for creating or updating a patient.
 * Includes validation constraints to ensure data integrity upon submission.
 */
@Data
public class PatientForm {

    /**
     * The unique identifier of the patient. May be null for new patients.
     */
    private Long id;

    /**
     * The patient's card ID or identifier. Must not be blank.
     */
    @NotBlank(message = "Card ID is required")
    private String cardId;

    /**
     * The patient's medical condition or triage level. Must not be null.
     * See {@link Condition} for possible values.
     */
    @NotNull(message = "Condition is required")
    private Condition condition;

    /**
     * Flag indicating whether the patient is currently being processed. Must not be null.
     * Note: {@code @NotBlank} is not applicable to boolean primitives; {@code @NotNull} is sufficient for validation.
     */
    @NotNull(message = "BeingProcessed must be either true or false")
    @NotBlank(message = "BeingProcessed must be either true or false")
    private boolean beingProcessed;

    /**
     * Default no-argument constructor.
     */
    public PatientForm() {
    }
}