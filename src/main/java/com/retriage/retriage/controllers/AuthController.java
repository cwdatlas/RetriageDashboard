package com.retriage.retriage.controllers;

import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.models.AuthRequest;
import com.retriage.retriage.services.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for handling authentication-related requests.
 * This class provides endpoints for user login and potentially other authentication flows,
 * utilizing JWT for managing user sessions.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    /**
     * Constructs an instance of {@code AuthController}.
     *
     * @param jwtUtil The utility class for handling JSON Web Tokens.
     */
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Handles user login and generates a JWT token upon successful authentication.
     * It expects an authentication request body containing a token (presumably from an external
     * identity provider like Okta). It extracts roles from the token and, if successful,
     * redirects the user to the index page. If no roles are found, it returns a bad request response.
     *
     * @param authRequest Contains the token received from the identity provider.
     * @return A {@link ResponseEntity} indicating the result of the authentication attempt.
     * Returns HTTP 303 (See Other) with a Location header on success,
     * or HTTP 400 (Bad Request) with an {@link ErrorResponse} body if roles are missing.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        // Extract roles from the token
        String token = authRequest.getToken(); // Token sent from Okta
        List<String> roles = jwtUtil.extractRoles(token);

        if (roles.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("User has no assigned roles"),
                    HttpStatus.BAD_REQUEST.value(),
                    "USER_ROLE_NOT_FOUND"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.FOUND) // Using FOUND (302) or SEE OTHER (303) for redirect after POST
                .header("Location", "/index.html")
                .build();
    }
}