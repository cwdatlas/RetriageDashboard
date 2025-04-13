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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * JwtUtil
 * <br><br>
 * Utility class for generating, validating, and parsing JSON Web Tokens (JWT).
 * Uses HS256 symmetric signing and stores roles/username in token claims.
 * @Author: John Botonakis
 */
@Service
public class JwtUtil {
    // Secret key used to sign abd verify the JWT. It's generated using HS256 algorithm.
    // Replace this with a secure, long key from an environment variable in production
    private static final String SECRET = System.getenv("JWT_SECRET");
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    // Expiration time for the JWT, set to 1 hour (in milliseconds).
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // Expires after 1 hour
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);


    /**
     * Generates a JWT for the specified username and roles.
     *
     * @param username the user to include as the subject
     * @param roles the list of roles to include as a claim
     * @return a signed JWT string
     */
    public String generateToken(String username, List<String> roles) {
        log.info("Generating JWT token for user: {} with roles: {}", username, roles);
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the structure and signature of a JWT.
     *
     * @param token the JWT string to validate
     * @return true if valid; false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() // Starts building a JWT parser.
                    .setSigningKey(SECRET_KEY) // Sets the signing key for validation.
                    .build() // Actually builds the JWT parser.
                    .parseClaimsJws(token); // Parses and validates the JWT.
            return true; // If parsing and validation succeed, then the token is valid.
        } catch (JwtException | IllegalArgumentException e) {
            return false; // If any exception occurs during parsing or validation, then the token is invalid.
        }
    }

    /**
     * Extracts the username from a JWT.
     *
     * @param token The JWT from which to extract the username.
     * @return The username extracted from the JWT.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder() // Starts building a JWT parser.
                .setSigningKey(SECRET_KEY) // Sets the signing key for parsing.
                .build() // Builds the JWT parser.
                .parseClaimsJws(token) // Parses and validates the JWT.
                .getBody() // Gets the body of the parsed JWT.
                .getSubject(); // Gets the subject (username) from the body.
    }

    /**
     * Extracts a list of roles from the "roles" claim in the JWT.
     *
     * @param token the JWT string
     * @return a list of roles, or empty list if none found
     */
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Ensure roles claim is a list and contains only strings
        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof List<?> rawList) {
            List<String> roles = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof String) {
                    roles.add((String) item);
                } else {
                    // Handle the case where an item is not a String.
                    System.err.println("Warning: Non-string role found in JWT: " + item);
                }
            }
            return roles;
        }
        return Collections.emptyList(); // Or handle the case where "roles" claim is not a List
    }

    /**
     * Retrieves the secret signing key used for both signing and verification.
     *
     * @return the shared symmetric key
     */
    private Key getSigningKey() {
        return SECRET_KEY;
    }
}