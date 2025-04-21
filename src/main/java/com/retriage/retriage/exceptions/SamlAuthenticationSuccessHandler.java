package com.retriage.retriage.exceptions;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.JwtUtil;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Handles successful SAML2 authentication by generating a JWT for the authenticated user.
 * This class overrides the default success handler behavior to return a JSON response
 * containing a JWT, instead of redirecting to a default URL.
 */
@Component
public class SamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(SamlAuthenticationSuccessHandler.class);
    private final JwtUtil jwtUtil;
    private final UserService userService;
    @Value("${DOMAIN}")
    private String domain;

    /**
     * Constructor for injecting the JWT utility used for token generation.
     *
     * @param jwtUtil The JwtUtil instance used to generate JWT tokens.
     */
    public SamlAuthenticationSuccessHandler(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Called when a user has been successfully authenticated via SAML.
     * Generates a JWT and returns it in a JSON response.
     *
     * @param request        The HTTP request.
     * @param response       The HTTP response.
     * @param authentication The authenticated user.
     * @throws IOException      If writing to the response fails.
     * @throws ServletException If the request cannot be handled.
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

        logger.info("SAML success - Cookies set for {}", email);
        response.sendRedirect("/index.html");
    }

    /**
     * Creates a secure, HTTP-only cookie with the specified name and value.
     * <p>
     * This cookie is configured for use in cross-site contexts with the following settings:
     * - Path: "/" to make it available across the entire app
     * - Domain: Passed in as environment variable (adjust in production)
     * - HttpOnly: true to prevent JavaScript access
     * - Secure: true to ensure it is only sent over HTTPS
     * - MaxAge: 1 hour (3600 seconds)
     * - SameSite: "None" to allow cross-site requests (required for cookies with Secure flag)
     *
     * @param name  the name of the cookie
     * @param value the value to store in the cookie
     * @return a configured {@link Cookie} instance
     */
    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");                       // Allow cookie access across all paths
        cookie.setDomain(domain);             // Domain for which the cookie is valid
        cookie.setHttpOnly(true);                  // Prevent JavaScript access (XSS protection)
        cookie.setSecure(true);                    // Transmit only over HTTPS
        cookie.setMaxAge(60 * 60);                 // Set expiration time to 1 hour
        cookie.setAttribute("SameSite", "None");   // Allow cross-site cookie usage (required for Secure + third-party)
        return cookie;
    }


}
