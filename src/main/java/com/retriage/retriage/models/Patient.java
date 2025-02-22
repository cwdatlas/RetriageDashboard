package com.retriage.retriage.models;

import com.retriage.retriage.enums.Condition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "patients")
public class Patient {
    //TODO update model to match User from the database model
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Condition condition;
    @Getter
    @Setter
    @OneToOne
    private Resource resultType;


    // Default constructor (required by JPA)
    public Patient() {
    }

    public Patient(String firstName, String lastName, Condition condition, Resource resultType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.condition = condition;
        this.resultType = resultType;
    }
}
