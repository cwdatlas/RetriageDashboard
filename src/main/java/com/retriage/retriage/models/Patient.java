package com.retriage.retriage.models;

import com.retriage.retriage.enums.Condition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Card ID cannot be blank")
    @Column(unique = true, nullable = false)
    private String cardId;

    // Use a safer column name instead of 'condition'
    @NotNull(message = "Condition is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "patient_condition")
    private Condition Condition;

    @NotNull(message = "BeingProcessed must be either true or false")
    private boolean processed;

    public Patient() {
    }
}
