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

/**
 * REST controller for managing user-related API requests.
 * Provides endpoints for retrieving information about the currently authenticated user,
 * typically based on a JWT provided in the Authorization header or a cookie.
 * Handles cross-origin requests via {@link CrossOrigin}.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    /**
     * The service responsible for handling user business logic.
     */
    private final UserService userService;
    /**
     * The utility class for handling JSON Web Tokens.
     */
    private final JwtUtil jwtUtil;

    /**
     * Constructs an instance of {@code UserController}.
     *
     * @param userService The service for managing users.
     * @param jwtUtil     The utility class for handling JSON Web Tokens.
     */
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Retrieves the currently authenticated user's information based on a JWT provided
     * in the {@code Authorization} header (Bearer token).
     * Validates the token and retrieves user details from the database.
     * Accessible via GET requests to {@code /api/users/me}.
     *
     * @param authHeader The Authorization header value, expected in "Bearer [token]" format.
     * @return A {@link ResponseEntity} containing the user data as a {@link UserDto} on success (HTTP 200 OK),
     * or an error response (HTTP 400 Bad Request, HTTP 401 Unauthorized) if the header or token is invalid,
     * or the user is not found.
     */
    @CrossOrigin // Note: @CrossOrigin is also on the class level, this might be redundant but kept as in original code.
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // If there's no Auth header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("getCurrentUser - Missing or malformed Authorization header.");
            return ResponseEntity.badRequest().body("Authorization header missing or invalid");
        }
        //  Strip "Bearer " prefix
        String token = authHeader.substring(7);

        // Token structurally invalid or expired/invalid signature
        if (!jwtUtil.validateToken(token)) {
            logger.warn("getCurrentUser - Token failed validation: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Extract username (email)
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
     * Returns user information extracted from a valid "token" cookie in the request.
     * If the token cookie is missing or the token within it is invalid, returns an error response.
     * Accessible via GET requests to {@code /api/users/me}.
     *
     * @param request the incoming HTTP request containing cookies.
     * @return A {@link ResponseEntity} containing the authenticated user's data as a {@link UserDto} on success (HTTP 200 OK),
     * or an error response (HTTP 401 Unauthorized) if no cookies are received,
     * the "token" cookie is not found, or the token is invalid or user not found.
     */
    // This overloaded method serves requests that provide the token via cookie instead of header.
    // It has the same mapping "/me" but a different method signature.
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;

        // No Cookies
        if (cookies == null) {
            logger.warn("getCurrentUser - No cookies received");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No cookies received"));
        }

        // Looking for 'token' cookie
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
        if (email == null || email.isBlank()) {
            logger.warn("getCurrentUser - Could not extract email from token in cookie.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token structure");
        }

        User user = userService.getUserByEmail(email);
        if (user == null) {
            logger.warn("getCurrentUser - No user found for email extracted from cookie token: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return ResponseEntity.ok(new UserDto(
                email,
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        ));

    }
}