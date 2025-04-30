package com.retriage.retriage.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

/**
 * JwtUtil
 * Utility class for generating, validating, and parsing JSON Web Tokens (JWT).
 * Uses HS256 symmetric signing and stores roles/username in token claims.
 * Provides methods for creating tokens with user details and extracting information from them.
 *
 * @author John Botonakis
 */
@Service
public class JwtUtil {
    /**
     * The secret key string used for signing and verifying the JWT.
     * Loaded from the environment variable "JWT_SECRET" if available, otherwise uses a default development key.
     * In production, this should always be a secure, randomly generated key stored securely.
     */
    // Secret key used to sign abd verify the JWT. It's generated using HS256 algorithm.
    private static final String SECRET = Optional.ofNullable(System.getenv("JWT_SECRET"))
            .orElse("DEV_FAKE_SECRET_BUT_AT_LEAST_32_CHARACTERS_LONG!");

    /**
     * The signing key derived from the {@link #SECRET} string, used for JWT operations.
     */
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    /**
     * The expiration time for the JWT, defined in milliseconds. Currently set to 1 hour.
     */
    // Expiration time for the JWT, set to 1 hour (in milliseconds).
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // Expires after 1 hour
    /**
     * Logger for this utility class.
     */
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);


    /**
     * Generates a JWT for the specified username and roles.
     * The token includes the username as the subject, roles as a custom claim,
     * the issue date, and an expiration date. It is signed using the configured secret key.
     *
     * @param username the user's identifier (e.g., email) to be set as the subject of the JWT.
     * @param roles    a list of strings representing the user's roles to be included as a "roles" claim in the JWT.
     * @return a signed JWT string.
     */
    public String generateToken(String username, List<String> roles) {
        log.info("Generates JWT token for user: {} with roles: {}", username, roles);
        return Jwts.builder()
                .setSubject(username) // Sets the subject claim (typically the user identifier)
                .claim("roles", roles) // Sets a custom claim named "roles" with the list of roles
                .setIssuedAt(new Date()) // Sets the issued at time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Sets the expiration time
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Signs the JWT with the secret key using HS256 algorithm
                .compact(); // Builds and compacts the JWT into a string
    }

    /**
     * Validates the structure, signature, and expiration of a JWT.
     *
     * @param token the JWT string to validate.
     * @return true if the token is valid (correct format, signature, and not expired), false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() // Starts building a JWT parser.
                    .setSigningKey(SECRET_KEY) // Sets the signing key for validation.
                    .build() // Actually builds the JWT parser.
                    .parseClaimsJws(token); // Parses and validates the JWT. Throws JwtException if invalid.
            return true; // If parsing and validation succeed, then the token is valid.
        } catch (JwtException | IllegalArgumentException e) {
            // Catch any exception during parsing or validation (e.g., signature mismatch, expired token, malformed token)
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false; // If any exception occurs during parsing or validation, then the token is invalid.
        }
    }

    /**
     * Extracts the username (subject) from a JWT.
     * Requires the token to be valid.
     *
     * @param token The JWT string from which to extract the username.
     * @return The username string extracted from the JWT's subject claim.
     * @throws JwtException if the token is invalid or parsing fails.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder() // Starts building a JWT parser.
                .setSigningKey(SECRET_KEY) // Sets the signing key for parsing.
                .build() // Builds the JWT parser.
                .parseClaimsJws(token) // Parses and validates the JWT, throws if invalid.
                .getBody() // Gets the body of the parsed JWT (Claims).
                .getSubject(); // Gets the subject (username) from the claims.
    }

    /**
     * Extracts a list of roles from the "roles" claim in the JWT.
     * Assumes the "roles" claim exists and is stored as a list of strings.
     * If the claim is missing or not a list of strings, an empty list is returned.
     *
     * @param token the JWT string from which to extract roles.
     * @return a list of role strings found in the "roles" claim. Returns an empty list if the claim is missing, not a list, or contains non-string elements.
     * @throws JwtException if the token is invalid or parsing fails (before accessing claims).
     */
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) // Parses and validates the JWT. Throws JwtException if invalid.
                .getBody(); // Gets the claims from the JWT body.

        // Attempt to get the "roles" claim and cast it to a List.
        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof List<?> rawList) {
            List<String> roles = new ArrayList<>();
            // Iterate through the raw list and add only String elements to the roles list.
            for (Object item : rawList) {
                if (item instanceof String) {
                    roles.add((String) item);
                } else {
                    // Log a warning if a non-string element is found in the expected roles list.
                    log.warn("Non-string element found in 'roles' claim: {}", item);
                    // Optionally, you might choose to throw an exception here if strict type checking is required.
                }
            }
            return roles;
        }
        // Return an empty list if the "roles" claim is not present or is not a List.
        return Collections.emptyList();
    }

    /**
     * Retrieves the secret signing key used for both signing and verification.
     *
     * @return the shared symmetric {@link Key}.
     */
    private Key getSigningKey() {
        return SECRET_KEY;
    }
}