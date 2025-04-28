package com.retriage.retriage.services;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthenticationFilter
 * <br><br>
 * A custom Spring Security filter that intercepts incoming requests to:
 * <ul>
 * <li>Extract a JWT from the {@code Authorization} header (Bearer token) or a "token" cookie.</li>
 * <li>Validate the extracted JWT.</li>
 * <li>If the JWT is valid, authenticate the user by setting the authenticated user in the Spring Security {@code SecurityContext}.</li>
 * <li>Allow the request to proceed down the filter chain if authentication is successful or if no token is found.</li>
 * <li>Send an unauthorized error response if the token is invalid or expired.</li>
 * </ul>
 * This filter ensures that requests requiring authentication have a valid JWT.
 *
 * @Author: John Botonakis
 */
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * Logger for this filter.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    /**
     * Utility class for performing JWT operations like validation and claim extraction.
     */
    private final JwtUtil jwtUtil;

    /**
     * Constructs a {@code JwtAuthenticationFilter} with the necessary JWT utility.
     *
     * @param jwtUtil Utility class for handling JWT operations.
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Performs the core filtering logic for each request.
     * <p>
     * This method attempts to extract a JWT from the {@code Authorization} header first.
     * If not found, it looks for a "token" cookie.
     * If a token is found, it validates it using {@link JwtUtil}.
     * Upon successful validation, it extracts the username and roles, creates a Spring Security
     * {@link UsernamePasswordAuthenticationToken}, sets its details, and places it in the
     * {@link SecurityContextHolder}.
     * Handles expired or invalid tokens by sending an unauthorized error response.
     * If no token is present, the filter chain proceeds without authentication being set by this filter.
     *
     * @param request  The incoming HTTP request.
     * @param response The outgoing HTTP response.
     * @param chain    The filter chain to proceed with.
     * @throws ServletException if a servlet-specific error occurs during filter processing.
     * @throws IOException      if an I/O error occurs during filter processing or response handling.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String token = null;

        // 1. Try to get token from Authorization header (Bearer token)
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Strip "Bearer " prefix
            logger.debug("doFilterInternal - Token extracted from Authorization header.");
        }

        // 2. If not in header, try to get token from "token" cookie
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("doFilterInternal - Token extracted from cookie.");
                    break; // Stop searching cookies once found
                }
            }
        }

        // 3. If no token found in either location, continue without setting authentication
        if (token == null) {
            logger.debug("doFilterInternal - No token found in header or cookies. Proceeding without authentication.");
            chain.doFilter(request, response); // Pass the request down the chain
            return; // Exit the method
        }

        // 4. Validate and set authentication if a token was found
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);

                // Convert role strings to SimpleGrantedAuthority objects required by Spring Security
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Assuming roles in token are e.g. "Director", convert to "ROLE_Director"
                        .toList();

                // Create Spring Security UserDetails object (using a basic User implementation)
                // Password is null/empty as it's not used in JWT authentication
                User user = new User(username, "", authorities);

                // Create an authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);

                // Set details from the HTTP request (e.g., IP address, session ID)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication object in the SecurityContextHolder
                // This indicates to Spring Security that the current request is authenticated as this user
                SecurityContextHolder.getContext().setAuthentication(authToken);

                logger.debug("doFilterInternal - JWT token validated and authentication set for user: {}", username);
            } else {
                // Token was present but failed validation (e.g., invalid signature)
                logger.warn("doFilterInternal - JWT token validation failed.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
                return; // Stop processing this request further down the chain on invalid token
            }
        } catch (ExpiredJwtException e) {
            // Handle token expiration specifically
            logger.warn("doFilterInternal - JWT token expired: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired.");
            return; // Stop processing this request further down the chain on expired token
        } catch (Exception e) {
            // Catch any other exceptions during token processing (e.g., malformed token)
            logger.error("doFilterInternal - Error processing JWT token: {}", e.getMessage(), e); // Log the exception details
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error processing token.");
            return; // Stop processing this request further down the chain on token processing error
        }

        // 5. Continue filter chain if authentication was successful or no token was found initially
        // (Note: If an error response was sent above, the filter chain processing stops immediately due to the `return;`)
        chain.doFilter(request, response);
    }
}