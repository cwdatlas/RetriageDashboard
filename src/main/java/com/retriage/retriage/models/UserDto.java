package com.retriage.retriage.models;

import com.retriage.retriage.enums.Role;
import lombok.Data;
import lombok.Getter;

/**
 * A Data Transfer Object (DTO) representing a simplified view of a User.
 * This is used primarily for returning user identity and role information
 * to the frontend without exposing full internal model details.
 */
@Getter
@Data
public class UserDto {
    private String username;
    private Role role;
    private String firstname;
    private String lastname;

    /**
     * Constructs a UserDto with the provided username and roles.
     *
     * @param username The user's unique identifier (usually email).
     * @param role     The role assigned to the user.
     */
    public UserDto(String username, String firstname, String lastname, Role role) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserDto{username='%s', role=%s, firstName='%s', lastName='%s'}".formatted(username, role, firstname, lastname);
    }
}