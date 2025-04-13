package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.models.User;
import com.retriage.retriage.models.UserDto;
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
import java.util.Map;
import java.util.Objects;

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

    @RequestMapping("/")
    public ResponseEntity<?> oktaLogin(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        // If token cookie already exists, skip everything
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    logger.info("oktaLogin - Token cookie already exists. Redirecting to /index.html");
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .header("Location", "/index.html")
                            .build();
                }
            }
        }

        logger.info("oktaLogin - No token cookie found. Redirecting to /index.html without changes.");
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // Extract token from cookies
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies == null) {
            logger.warn("getCurrentUser - No cookies recieved");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","No cookies recieved"));
        }
        for (Cookie cookie : cookies) {
            logger.info("getCurrentUser - Found cookie: {}={}", cookie.getName(), cookie.getValue());
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null) {
            logger.warn("getCurrentUser - Token not found in cookies");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing token"));
        }


        if (!jwtUtil.validateToken(token)) {
            logger.warn("getCurrentUser - Token failed validation: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }


        String email = jwtUtil.extractUsername(token);
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(new UserDto(
                email,
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        ));

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

}