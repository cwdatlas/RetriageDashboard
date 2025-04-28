package com.retriage.retriage.exceptions;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.JwtUtil;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Handles successful SAML2 authentication by generating a JWT for the authenticated user.
 * This class extends {@link SavedRequestAwareAuthenticationSuccessHandler} and overrides the default behavior
 * to process SAML attributes, create or update a local user record, generate a JWT,
 * set it as an HTTP-only cookie, and redirect the client to the application's index page.
 *
 * @Author: John Botonakis
 * @Author: With help provided by Matt Raible (<a href="https://developer.okta.com/blog/2022/08/05/spring-boot-saml">...</a>)
 */
@Component
public class SamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(SamlAuthenticationSuccessHandler.class);
    /**
     * The utility class for handling JSON Web Tokens.
     */
    private final JwtUtil jwtUtil;
    /**
     * The service for managing user data in the database.
     */
    private final UserService userService;
    /**
     * The domain name used for setting the authentication cookie, injected from application properties.
     */
    @Value("${DOMAIN}")
    private String domain;

    /**
     * Constructor for injecting the JWT utility and User service.
     *
     * @param jwtUtil The {@link JwtUtil} instance used to generate JWT tokens.
     * @param userService The {@link UserService} instance used to manage user data.
     */
    public SamlAuthenticationSuccessHandler(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Called when a user has been successfully authenticated via SAML.
     * Extracts user attributes from the SAML principal, creates or updates the user
     * in the local database, generates a JWT containing user role information,
     * sets the JWT as an HTTP-only cookie in the response, and redirects the client
     * to the index page.
     *
     * @param request        The HTTP request.
     * @param response       The HTTP response.
     * @param authentication The {@link Authentication} object representing the authenticated user, expected to hold a {@link Saml2AuthenticatedPrincipal}.
     * @throws IOException      If writing to the response fails during redirect or error handling.
     * @throws ServletException If the request cannot be handled by the servlet container.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();

        String email = principal.getFirstAttribute("email");
        String firstname = principal.getFirstAttribute("firstname");
        String lastname = principal.getFirstAttribute("lastname");
        List<String> groups = principal.getAttribute("groups");

        if (email == null) {
            logger.warn("onAuthenticationSuccess - Missing email attribute. Cannot continue.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing email.");
            return;
        }

        if (firstname == null) firstname = "Guesty";
        if (lastname == null) lastname = "McGuestFace";
        if (groups == null || groups.isEmpty()) groups = List.of("Guest");

        // Convert first matching group to Role
        Role userRole = Role.Guest;
        for (Role role : Role.values()) {
            if (groups.contains(role.name())) {
                userRole = role;
                break;
            }
        }

        // Save user if not already in DB
        if (userService.getUserByEmail(email) == null) {
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setRole(userRole);
            userService.saveUser(user);
            logger.info("New user saved: {}", email);
        }

        // Generate JWT with role info
        String jwt = jwtUtil.generateToken(email, List.of(userRole.name()));

        response.addCookie(createCookie("token", jwt));
        Cookie cookie = new Cookie("domain", domain);
        cookie.setPath("/");
        cookie.setDomain(domain);
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        cookie.setMaxAge(60*60*24);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);

        logger.info("SAML success - Cookies set for {}", email);
        // Redirect to the frontend entry point after setting the cookie
        response.sendRedirect("/index.html");
    }

    /**
     * Creates a secure, HTTP-only cookie with the specified name and value.
     * <p>
     * This cookie is configured for use in cross-site contexts with the following settings:
     * <ul>
     * <li>Path: "/" to make it available across the entire app</li>
     * <li>Domain: Set based on the {@code domain} property (injected via {@code @Value})</li>
     * <li>HttpOnly: true to prevent JavaScript access (XSS protection)</li>
     * <li>Secure: true to ensure it is only sent over HTTPS</li>
     * <li>MaxAge: 1 hour (3600 seconds)</li>
     * <li>SameSite: "None" to allow cross-site cookie usage (required when Secure is true for third-party contexts)</li>
     * </ul>
     *
     * @param name  the name of the cookie
     * @param value the value to store in the cookie (e.g., the JWT)
     * @return a configured {@link Cookie} instance ready to be added to the response
     */
    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");                       // Allow cookie access across all paths
        cookie.setDomain(domain);             // Domain for which the cookie is valid
        cookie.setHttpOnly(true);                  // Prevent JavaScript access (XSS protection)
        cookie.setSecure(true);                    // Transmit only over HTTPS
        cookie.setMaxAge(60 * 60);                 // Set expiration time to 1 hour (in seconds)
        cookie.setAttribute("SameSite", "None");   // Allow cross-site cookie usage (required for Secure + third-party contexts like SAML)
        return cookie;
    }


}