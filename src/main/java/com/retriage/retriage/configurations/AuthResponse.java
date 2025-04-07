package com.retriage.retriage.configurations;

import java.util.List;

/**
 * Represents the authentication response, containing the JWT token and user roles.
 * Used to send authentication details back to the client after successful login.
 */
public class AuthResponse {
    private final String token; // JWT authentication token.
    private final List<String> roles; // List of user roles associated with the token.

    /**
     * Constructor for AuthResponse.
     *
     * @param token The JWT authentication token.
     * @param roles The list of user roles associated with the token.
     */
    public AuthResponse(String token, List<String> roles) {
        this.token = token;
        this.roles = roles;
    }

    /**
     * Gets the JWT authentication token.
     *
     * @return The JWT token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets the list of user roles associated with the token.
     *
     * @return The list of user roles.
     */
    public List<String> getRoles() {
        return roles;
    }
}
