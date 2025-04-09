package com.retriage.retriage.exceptions;

import com.retriage.retriage.services.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * Handles successful SAML2 authentication by generating a JWT for the authenticated user.
 * This class overrides the default success handler behavior to return a JSON response
 * containing a JWT, instead of redirecting to a default URL.
 */
@Component
public class SamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    /**
     * Constructor for injecting the JWT utility used for token generation.
     *
     * @param jwtUtil The JwtUtil instance used to generate JWT tokens.
     */
    public SamlAuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
        String username = authentication.getName(); // Subject (email) from Okta SAML
        String jwt = jwtUtil.generateToken(username); // Generate JWT with username as subject

        // Respond with JWT in JSON format
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
        response.getWriter().flush();

        // No redirect or default behavior
    }
}
