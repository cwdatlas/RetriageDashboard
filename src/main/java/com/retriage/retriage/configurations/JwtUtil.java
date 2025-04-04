package com.retriage.retriage.configurations;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtUtil {
    // Secret key used to sign the JWT. It's generated using HS256 algorithm.
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Expiration time for the JWT, set to 1 hour (in milliseconds).
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // Expires after 1 hour

    /**
     * Generates a JWT (JSON Web Token) for the given username.
     *
     * @param username The username to be included in the JWT subject.
     * @return The generated JWT as a String.
     */
    public String generateToken(String username) {
        return Jwts.builder() //Starts building the JWT
                .setSubject(username) // Sets the subject (usually the user identifier).
                .setIssuedAt(new Date()) // Sets the issue time of the token.
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Sets the expiration time.
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Signs the JWT with the secret key and HS256 algorithm.
                .compact(); // Compresses the JWT into a URL-safe string.
    }

    /**
     * Validates a JWT.
     *
     * @param token The JWT to validate.
     * @return True if the token is valid, false otherwise.
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
     * Extracts the roles from a JWT token.
     *
     * @param token The JWT token to extract roles from.
     * @return A list of roles extracted from the token's claims.
     */
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Retrieves the signing key for parsing.
                .build()
                .parseClaimsJws(token) // Parses and validates the JWT.
                .getBody();

        return claims.get("roles", List.class); // Extracts the "roles" claim as a List of Strings.
    }

    /**
     * Retrieves the signing key used to verify the JWT.
     *
     * @return The signing key as a Key object.
     */
    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getEncoded();
        return Keys.hmacShaKeyFor(keyBytes); // Creates an HMAC SHA key from the decoded bytes.
    }
}