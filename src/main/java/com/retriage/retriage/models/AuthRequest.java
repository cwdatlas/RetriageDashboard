package com.retriage.retriage.models;

/**
 * Represents the authentication request, containing the JWT token.
 * Used to receive authentication details from the client, typically after
 * an external authentication process (like SAML).
 */
public class AuthRequest {
    /**
     * The JSON Web Token (JWT) used for authentication.
     */
    private String token; // JWT authentication token.

    /**
     * Constructs a new {@code AuthRequest}.
     * Note: A default no-argument constructor is implicitly provided by the Java compiler
     * if no other constructors are defined. Lombok's @Data would also generate one.
     * Explicitly adding a constructor here for clarity if needed, or rely on default/Lombok.
     * <p>
     * public AuthRequest() {
     * // Default constructor
     * }
     * <p>
     * Constructs a new {@code AuthRequest} with the specified token.
     *
     * @param token The JWT authentication token.
     * <p>
     * public AuthRequest(String token) {
     * this.token = token;
     * }
     */

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