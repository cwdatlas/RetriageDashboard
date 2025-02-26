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

    @OneToMany(mappedBy="retriageNurse",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Patient> createdPatients;

    public String getName() {
        return firstName + " " + lastName;
    }
}


