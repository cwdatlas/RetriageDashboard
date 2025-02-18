package com.retriage.retriage.models;
import jakarta.persistence.*;

@Entity
@Table(name = "director")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String status;

    // Default constructor (required by JPA)
    public Director() {
    }

    // Optional convenience constructor
    public Director(String firstName, String lastName, String status) {
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

    //Active director - Actively running a sim
    //Inactive director - Someone who is just watching
    public void setStatus(String status) {
        this.status = status;
    }
}


