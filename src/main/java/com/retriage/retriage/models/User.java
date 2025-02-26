package com.retriage.retriage.models;

import com.retriage.retriage.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor // Replaces the empty constructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    private String firstName;

    private String lastName;

    private Role role;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Patient> getCreatedPatients() {
        return createdPatients;
    }

    public void setCreatedPatients(List<Patient> createdPatients) {
        this.createdPatients = createdPatients;
    }

    @OneToMany(mappedBy="retriageNurse",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Patient> createdPatients;

    public String getName() {
        return firstName + " " + lastName;
    }
}


