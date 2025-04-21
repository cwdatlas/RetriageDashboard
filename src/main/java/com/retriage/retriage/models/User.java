package com.retriage.retriage.models;

import com.retriage.retriage.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a user within the application.
 * This class is a JPA entity mapped to the "users" table in the database.
 * It stores basic user profile information and their assigned role, used for authentication and authorization.
 */
@Data
@Entity
@Table(name = "users")
public class User {
    /**
     * The unique identifier for the user. This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's email address. Used as a unique identifier for login.
     * Must not be blank or null and must be a valid email format.
     */
    @NotBlank(message = "Email cannot be blank") // Validation: Not blank
    @Email(message = "Email should be valid") // Validation: Valid email format
    @NotNull(message = "Email can't be null!") //Validation: Not null
    private String email;

    /**
     * The user's first name. Must not be blank or null.
     */
    @NotBlank(message = "First name cannot be blank") // Validation: Not blank
    @NotNull(message = "First name cannot be null") // Validation: Not null
    private String firstName;

    /**
     * The user's last name. Must not be blank or null.
     */
    @NotBlank(message = "Last name cannot be blank") // Validation: Not blank
    @NotNull(message = "Last name cannot be null") // Validation: Not null
    private String lastName;

    /**
     * The role assigned to the user, determining their permissions within the application.
     * Must not be null. See {@link Role} enum.
     */
    @NotNull(message = "Role cannot be null") // Validation: Not null
    private Role role;

    /**
     * Default no-argument constructor required by JPA.
     */
    public User() {
    }
}