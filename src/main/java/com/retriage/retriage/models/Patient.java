package com.retriage.retriage.models;

import com.retriage.retriage.enums.Condition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a patient within an event.
 * This class is a JPA entity mapped to the "patients" table in the database.
 * It stores key information about a patient, including their identifier, medical condition,
 * and processing status.
 */
@Data
@Entity
@Table(name = "patients")
public class Patient {

    /**
     * The unique identifier for the patient. This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A unique identifier for the patient, often represented as a card ID.
     * This field is required and must be unique across all patients.
     */
    @NotBlank(message = "Card ID cannot be blank")
    @Column(nullable = false)
    private String cardId;

    /**
     * The medical condition or triage level of the patient.
     * This field is required and is stored as a string in the database.
     * See {@link Condition} for possible values.
     */
    @NotNull(message = "Condition is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "patient_condition")
    private Condition Condition;

    /**
     * A flag indicating whether the patient is currently being processed in a patient pool.
     * This field is required.
     */
    @NotNull(message = "BeingProcessed must be either true or false")
    private boolean processed;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Patient() {
    }
}