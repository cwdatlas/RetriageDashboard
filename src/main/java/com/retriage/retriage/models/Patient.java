package com.retriage.retriage.models;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String status;

    // Default constructor (required by JPA)
    public Patient() {
    }

    // Optional convenience constructor
    public Patient(String firstName, String lastName, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
