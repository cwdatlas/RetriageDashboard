package com.retriage.retriage.controllers;

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

/**
 * PageController.java
 * <br></br>
 * Controller for handling requests to the home page after successful SAML authentication.
 * This controller is responsible for displaying user information retrieved from the
 * {@link Saml2AuthenticatedPrincipal} after the user has been authenticated via SAML.
 *
 * @Author: John Botonakis
 * @Author: With help provided by Matt Raible (<a href="https://developer.okta.com/blog/2022/08/05/spring-boot-saml">...</a>)
 */
@Controller
@CrossOrigin
public class PageController {
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    PageController() {
    }

    /**
     * Handles requests to the root path ("/") after SAML login.
     * Redirects to index.html if the token cookie exists or does nothing otherwise.
     *
     * @param principal the authenticated SAML principal (maybe null)
     * @param request   the incoming HTTP request
     * @param response  the outgoing HTTP response
     * @return a redirect response to the frontend
     */
    @RequestMapping("/")
    public ResponseEntity<?> oktaLogin(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        // If token cookie already exists, the user is already logged in
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

        // If the token cookie is missing, there's nothing to do, so redirect anyway
        logger.info("oktaLogin - No token cookie found. Redirecting to /index.html without changes.");
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();
    }

    /**
     * Displays all cookies received in the current request.
     * Used for debugging token and user cookie propagation.
     *
     * @param request the incoming HTTP request
     * @return plain-text list of cookie names and values
     */
    @GetMapping("/api/debug/cookies")
    public ResponseEntity<?> showCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        // If there are no cookies present
        if (cookies == null) return ResponseEntity.ok("No cookies received.");

        // Lists out all the cookies
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append(cookie.getName()).append(" = ").append(cookie.getValue()).append("\n");
        }
        return ResponseEntity.ok(sb.toString());
    }

    @RequestMapping("/event")
    public ResponseEntity<?> eventPage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/event.html")
                .build();
    }
    @RequestMapping("/event/event_creation")
    public ResponseEntity<?> eventCreationPage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/event/eventcreation.html")
                .build();
    }
}