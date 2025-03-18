package com.retriage.retriage.models;

import com.retriage.retriage.enums.Condition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @NotBlank(message = "Card ID cannot be blank")
    @Column(unique = true, nullable = false)
    private String cardId;

    @Getter
    @Setter
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    // Use a safer column name instead of 'condition'
    @Getter
    @Setter
    @NotNull(message = "Condition is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "patient_condition")
    private Condition Condition;

    //Not Owner
    @Getter
    @Setter
    @ManyToMany(mappedBy = "patients")
    private List<PatientPool> resourceList;

    // Owner
    @Getter
    @Setter
    @NotNull(message = "A retriage nurse must be assigned")
    @ManyToOne
    private User retriageNurse;

    public Patient() {
    }
}
