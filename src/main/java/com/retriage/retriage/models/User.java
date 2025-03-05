package com.retriage.retriage.models;

import com.retriage.retriage.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email; // Import Email constraint
import jakarta.validation.constraints.NotBlank; // Import NotBlank constraint
import jakarta.validation.constraints.NotNull; // Import NotNull constraint
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @NotBlank(message = "Email cannot be blank") // Validation: Not blank
    @Email(message = "Email should be valid") // Validation: Valid email format
    private String email;

    @Getter
    @Setter
    @NotBlank(message = "First name cannot be blank") // Validation: Not blank
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "Last name cannot be blank") // Validation: Not blank
    private String lastName;

    @Getter
    @Setter
    @NotNull(message = "Role cannot be null") // Validation: Not null
    private Role role;

    //Not Owner
    @Getter
    @Setter
    @OneToMany(mappedBy="retriageNurse")
    private List<Patient> createdPatients;

    //Not Owner
    @Getter
    @Setter
    @OneToMany(mappedBy="director")
    private List<Event> createdEvents;

    public User(){
    }
}
