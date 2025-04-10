package com.retriage.retriage.services;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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
 * A custom Spring Security filter that:
 * - Extracts a JWT from the Authorization header
 * - Validates the JWT
 * - If valid, sets the authenticated user in the SecurityContext
 * @Author: John Botonakis
 */
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    /**
     * Constructor to inject the JwtUtil dependency.
     *
     * @param jwtUtil Utility class for handling JWT operations
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Core filtering method.
     * <p>
     * Checks if the request contains a valid JWT and, if so,
     * sets the authentication in the Spring Security context.
     *
     * @param request  incoming HTTP request
     * @param response outgoing HTTP response
     * @param chain    filter chain
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull  FilterChain chain
    ) throws ServletException, IOException {
        // Get the Authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Check if header is missing or does not contain a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("doFilterInternal - No Bearer token found in request header.");
            chain.doFilter(request, response); // Continue filter chain without setting auth
            return;
        }

        // Extract token string (remove "Bearer" prefix)
        final String token = authHeader.substring(7);

        try {
            // Validate the token
            if (jwtUtil.validateToken(token)) {
                // Extract username and roles
                String username = jwtUtil.extractUsername(token);
                String role = String.valueOf(jwtUtil.extractRoles(token));

                // Convert role to a GrantedAuthority (required by Spring Security)
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // Create a Spring Security user object
                User user = new User(username, "", authorities);

                // Wrap it in an authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);

                // Add web-specific details to the token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication into the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

                logger.debug("doFilterInternal - JWT token validated for user: {}", username);
            }
        } catch (ExpiredJwtException e) {
            // Token is expired
            logger.warn("doFilterInternal - JWT token expired.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired.");
            return;
        } catch (Exception e) {
            // Token is invalid or other error occurred
            logger.error("doFilterInternal - JWT token validation failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
            return;
        }

        // Continue filter chain
        chain.doFilter(request, response);
    }
}