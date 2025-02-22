package com.retriage.retriage.models;

import com.retriage.retriage.enums.Condition;
import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

//    TODO:  Go back and properly fix Error 1 (SQL Syntax Error):
//    RENAME the problematic column (currently named 'condition')
//    to something that is NOT a reserved SQL keyword (e.g., 'patientCondition', 'conditionType', etc.).

//    private Condition condition;


    // Default constructor (required by JPA)
    public Patient() {
    }

    // Getters and setters
    public Long getId() {

        return id;
    }

    // No setter for 'id' usually if it's auto-generated, but you can include it if needed.

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

//    public Condition getStatus() {
//        return condition;
//    }
//
//    public void setStatus(Condition condition) {
//        this.condition = condition;
//    }
}
