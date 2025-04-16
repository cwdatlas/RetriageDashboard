package com.retriage.retriage.controllers;

import com.retriage.retriage.models.User;
import com.retriage.retriage.models.UserDto;
import com.retriage.retriage.services.JwtUtil;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor injection of the service
     */
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @CrossOrigin
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // If there's no Auth header
        if (authHeader == null) {
            logger.warn("getCurrentUser - Missing or malformed Authorization header.");
            return ResponseEntity.badRequest().body("Authorization header missing or invalid");
        }
        //  Strip "Bearer " prefix
        String token = authHeader.substring(7);

        // Token structurally invalid
        if (!jwtUtil.validateToken(token)) {
            logger.warn("getCurrentUser - Token failed validation: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Extract username
        String email = jwtUtil.extractUsername(token);
        if (email == null || email.isBlank()) {
            logger.warn("getCurrentUser - Could not extract email from token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token structure");
        }

        // Look up user from DB
        User user = userService.getUserByEmail(email);
        if (user == null) {
            logger.warn("getCurrentUser - No user found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        // Return user info
        return ResponseEntity.ok(new UserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        ));
    }

    /**
     * Returns user information extracted from a valid token cookie.
     * If the token is missing or invalid, returns an error response.
     *
     * @param request the incoming HTTP request containing cookies
     * @return the authenticated user's data or error details
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;

        // No Cookies
        if (cookies == null) {
            logger.warn("getCurrentUser - No cookies received");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No cookies received"));
        }

        // Lookin for cookies
        for (Cookie cookie : cookies) {
            logger.info("getCurrentUser - Found cookie: {}={}", cookie.getName(), cookie.getValue());
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        // JWT Token inside the cookie is not found
        if (token == null) {
            logger.warn("getCurrentUser - Token not found in cookies");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing token"));
        }

        // JWT Token failing signature/expiration validation
        if (!jwtUtil.validateToken(token)) {
            logger.warn("getCurrentUser - Token failed validation: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Extract user info from the jwt token
        String email = jwtUtil.extractUsername(token);
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(new UserDto(
                email,
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        ));

    }
}