package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.JwtUtil;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeController.java
 * <br></br>
 * Controller for handling requests to the home page after successåful SAML authentication.
 * This controller is responsible for displaying user information retrieved from the
 * {@link Saml2AuthenticatedPrincipal} after the user has been authenticated via SAML.
 *
 * @Author: John Botonakis
 * @Author: With help provided by Matt Raible (<a href="https://developer.okta.com/blog/2022/08/05/spring-boot-saml">...</a>)
 */
@Controller
@CrossOrigin
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;
    AuthenticationPrincipal Saml2AuthenticatedPrincipal;

    HomeController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * home Mapping
     * <br></br>
     * Handles requests to the root path ("/") and displays the home page with user information.
     * <p>
     * This method is invoked when a user accesses the application's root URL. It retrieves the
     * {@link Saml2AuthenticatedPrincipal} representing the authenticated user, extracts user details
     * such as name, email address, and all attributes from the principal, and adds them to the
     * model for rendering in the "home" view.
     *
     * @param principal The {@link Saml2AuthenticatedPrincipal} representing the authenticated SAML user.
     *                  This is injected by Spring Security based on the current authentication context.
     * @param response  The Spring {@link HttpServletResponse} object used to add cookies.
     * @return A {@link ResponseEntity} that either redirects to the frontend or returns an error.
     */
    @RequestMapping("/")
    public ResponseEntity<?> oktaLogin(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, HttpServletResponse response) {
        List<String> roles = null;
        try {
            roles = principal.getAttribute("groups");
        } catch (NullPointerException e) {
            logger.warn("oktaLogin - User logged in without role: {}", principal != null ? principal.getName() : "UNKNOWN_USER_GROUP");
        }

        Role userRole = Role.Guest;
        List<String> errors = new ArrayList<>();

        if (roles == null) {
            logger.warn("oktaLogin - Access denied: Missing role.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(List.of("Access denied due to missing role."), HttpStatus.FORBIDDEN.value(), "ACCESS_DENIED"));
        } else {
            for (Role role : Role.values()) {
                if (roles.contains(role.toString())) {
                    userRole = role;
                    break;
                }
            }
        }

        String firstName = principal.getFirstAttribute("firstname");
        String lastName = principal.getFirstAttribute("lastname");
        String email = principal.getFirstAttribute("email");

        if (firstName == null) {
            errors.add("First name not provided.");
            firstName = "Guesty";
        }
        if (lastName == null) {
            errors.add("Last name not provided.");
            lastName = "McGuestFace";
        }
        if (email == null) {
            errors.add("Email not provided.");
            logger.warn("oktaLogin - Authentication failed: Missing email.");
            return new ResponseEntity<>(new ErrorResponse(errors, HttpStatus.UNAUTHORIZED.value(), "MISSING_EMAIL"), HttpStatus.UNAUTHORIZED);
        }

        if (userService.getUserByEmail(email) == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(userRole);
            userService.saveUser(newUser);
            logger.info("oktaLogin - New user saved with email: {}", email);
        }

        if (!errors.isEmpty()) {
            logger.warn("oktaLogin - Authentication failed: Validation errors.");
            return new ResponseEntity<>(new ErrorResponse(errors, HttpStatus.UNAUTHORIZED.value(), "AUTHENTICATION_ERROR"), HttpStatus.UNAUTHORIZED);
        }

        // Setting cookies using helper method

        response.addCookie(createCookie("firstname", firstName));
        response.addCookie(createCookie("lastname", lastName));
        response.addCookie(createCookie("email", email));
        response.addCookie(createCookie("role", userRole.toString()));
        String jwt = jwtUtil.generateToken(email);
        response.addCookie(createCookie("token", jwt));

        logger.info("oktaLogin - User authenticated and cookies set for email: {}", email);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // Extract token from cookies
        Cookie[] cookies = request.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token cookie");
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String email = jwtUtil.extractUsername(token);
        return ResponseEntity.ok("Token is valid for: " + email);
    }



    @GetMapping("/api/debug/cookies")
    public ResponseEntity<?> showCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return ResponseEntity.ok("No cookies received.");

        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append(cookie.getName()).append(" = ").append(cookie.getValue()).append("\n");
        }
        return ResponseEntity.ok(sb.toString());
    }


    /**
     * Helper method to create a cookie with standard settings.
     */
    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setMaxAge(60 * 60);
        cookie.setAttribute("SameSite", "Lax"); // 👈 allows cookie to be sent during navigation
        return cookie;
    }


}