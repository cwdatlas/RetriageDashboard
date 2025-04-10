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
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String token = null;

        // 1. Try to get token from Authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.debug("doFilterInternal - Token extracted from Authorization header.");
        }

        // 2. If not in header, try to get token from cookie
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("doFilterInternal - Token extracted from cookie.");
                    break;
                }
            }
        }

        // 3. If no token found, continue without setting authentication
        if (token == null) {
            logger.debug("doFilterInternal - No token found in header or cookies.");
            chain.doFilter(request, response);
            return;
        }

        // 4. Validate and set authentication
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                User user = new User(username, "", authorities);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                logger.debug("doFilterInternal - JWT token validated for user: {}", username);
            }
        } catch (ExpiredJwtException e) {
            logger.warn("doFilterInternal - JWT token expired.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired.");
            return;
        } catch (Exception e) {
            logger.error("doFilterInternal - JWT token validation failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
            return;
        }

        // 5. Continue filter chain
        chain.doFilter(request, response);
    }
}