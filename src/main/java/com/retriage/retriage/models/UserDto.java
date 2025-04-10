package com.retriage.retriage.models;

import java.util.List;

/**
 * A Data Transfer Object (DTO) representing a simplified view of a User.
 * This is used primarily for returning user identity and role information
 * to the frontend without exposing full internal model details.
 */
public class UserDto {
    private String username;
    private List<String> roles;

    /**
     * Constructs a UserDto with the provided username and roles.
     *
     * @param username The user's unique identifier (usually email).
     * @param roles    The list of roles assigned to the user.
     */
    public UserDto(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    // Getters & setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
