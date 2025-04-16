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

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Handles user login and generates a JWT token upon successful authentication.
     *
     * @param authRequest Contains the username and password (or other authentication details).
     * @return ResponseEntity containing the JWT token in the AuthResponse object.
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
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();
    }
}