package com.retriage.retriage.configurations;

/**
 * Represents the authentication request, containing the JWT token.
 * Used to receive authentication details from the client.
 */
public class AuthRequest {
    private String token; // JWT authentication token.

    /**
     * Gets the JWT authentication token.
     *
     * @return The JWT token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the JWT authentication token.
     *
     * @param token The JWT token to set.
     */
    public void setToken(String token) {
        this.token = token;
    }
}