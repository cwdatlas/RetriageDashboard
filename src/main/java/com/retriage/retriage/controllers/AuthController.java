package com.retriage.retriage.controllers;

import com.retriage.retriage.configurations.AuthRequest;
import com.retriage.retriage.configurations.AuthResponse;
import com.retriage.retriage.configurations.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // Generate a JWT token for the authenticated user.
        String token = jwtUtil.generateToken(authRequest.getUsername());

        // Return the token in the response.
        return ResponseEntity.ok(new AuthResponse(token));
    }
}