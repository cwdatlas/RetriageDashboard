package com.retriage.retriage.models;

import com.retriage.retriage.enums.Role;
import lombok.Data;
import lombok.Getter;

/**
 * A Data Transfer Object (DTO) representing a simplified view of a User.
 * This is used primarily for returning user identity and role information,
 * first name, and last name to the frontend without exposing full internal model details
 * from the {@link User} entity.
 */
@Getter
@Data // @Data includes @ToString, @EqualsAndHashCode, @Getter, @Setter (but @Getter is explicit)
public class UserDto {
    /**
     * The user's unique identifier, typically their email address.
     */
    private String username;
    /**
     * The role assigned to the user, defining their permissions. See {@link Role}.
     */
    private Role role;
    /**
     * The user's first name.
     */
    private String firstname;
    /**
     * The user's last name.
     */
    private String lastname;

    /**
     * Constructs a UserDto with the provided user details.
     *
     * @param username  The user's unique identifier (usually email).
     * @param firstname The user's first name.
     * @param lastname  The user's last name.
     * @param role      The role assigned to the user.
     */
    public UserDto(String username, String firstname, String lastname, Role role) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    /**
     * Provides a string representation of the UserDto, formatted for debugging or logging.
     *
     * @return A formatted string containing the username, role, first name, and last name.
     */
    @Override
    public String toString() {
        return "UserDto{username='%s', role=%s, firstName='%s', lastName='%s'}".formatted(username, role, firstname, lastname);
    }
}