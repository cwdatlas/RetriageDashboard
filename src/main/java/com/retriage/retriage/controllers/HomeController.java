package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.http.Cookie;
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
 * Controller for handling requests to the home page after successful SAML authentication.
 * This controller is responsible for displaying user information retrieved from the
 * {@link Saml2AuthenticatedPrincipal} after the user has been authenticated via SAML.
 *
 * @Author: John Botonakis
 * @PatientPool: With help provided by Matt Raible (https://developer.okta.com/blog/2022/08/05/spring-boot-saml)
 */
@Controller
@CrossOrigin
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserService userService;
    AuthenticationPrincipal Saml2AuthenticatedPrincipal;

    HomeController(UserService userService) {
        this.userService = userService;
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
            logger.warn("oktaLogin: Client logged in without role. set user {} role to guest.", principal.getName());
        }
        Role userRole = Role.Guest;
        List<String> errors = new ArrayList<>();

        if (roles == null) {
            logger.warn("oktaLogin: Client logged in without role. set user {} role to guest.", principal.getName());
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
            errors.add("First name not provided by authentication provider.");
            firstName = "Guest";
        }
        if (lastName == null) {
            errors.add("Last name not provided by authentication provider.");
            lastName = "Guest";
        }
        if (email == null) {
            errors.add("Email address not provided by authentication provider.");
            ErrorResponse errorResponse = new ErrorResponse(errors, HttpStatus.UNAUTHORIZED.value(), "MISSING_EMAIL");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // Body is ErrorResponse
        }

        if (userService.getUserByEmail(email) == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(userRole);
            userService.saveUser(newUser);
        }

        if (!errors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse(errors, HttpStatus.UNAUTHORIZED.value(), "AUTHENTICATION_ERROR");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // Body is ErrorResponse
        }

        Cookie fNameCookie = new Cookie("firstName", firstName);
        fNameCookie.setPath("/");
        fNameCookie.setDomain("localhost");
        fNameCookie.setHttpOnly(false);
        fNameCookie.setSecure(false);
        response.addCookie(fNameCookie);
        Cookie lNameCookie = new Cookie("lastName", lastName);
        lNameCookie.setPath("/");
        lNameCookie.setDomain("localhost");
        lNameCookie.setHttpOnly(false);
        fNameCookie.setSecure(false);
        response.addCookie(lNameCookie);
        Cookie roleCookie = new Cookie("role", userRole.toString());
        roleCookie.setPath("/");
        roleCookie.setDomain("localhost");
        roleCookie.setHttpOnly(false);
        roleCookie.setSecure(false);
        response.addCookie(roleCookie);
        Cookie emailCookie = new Cookie("email", email);
        emailCookie.setPath("/");
        emailCookie.setDomain("localhost");
        emailCookie.setHttpOnly(false);
        emailCookie.setSecure(false);
        response.addCookie(emailCookie);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:3000/")
                .build(); // No body in a 302 redirect
    }

//    @GetMapping("/logout")
//    public ResponseEntity<Void> logoutRedirect() {
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .header("Location", "http://localhost:3000/") // Redirect to home instead of /logout
//                .build();
//}
}