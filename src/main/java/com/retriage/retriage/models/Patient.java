package com.retriage.retriage.models;

import com.retriage.retriage.enums.Condition;
import jakarta.persistence.*;
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
    private String cardId;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    // Use a safer column name instead of 'condition'
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "patient_condition")
    private Condition Condition;

    //Not Owner
    @Getter
    @Setter
    @ManyToMany(mappedBy = "patients")
    private List<Resource> resourceList;

    // Owner
    @Getter
    @Setter
    @ManyToOne
    private User retriageNurse;

    public Patient() {
    }
}
