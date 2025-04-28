package com.retriage.retriage.controllers;

import com.retriage.retriage.services.JwtUtil;
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
 * Also handles redirects to other static pages like event and event creation pages.
 *
 * @Author: John Botonakis
 * @Author: With help provided by Matt Raible (<a href="https://developer.okta.com/blog/2022/08/05/spring-boot-saml">...</a>)
 */
@Controller
@CrossOrigin
public class PageController {
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    private final JwtUtil jwtUtil;

    /**
     * Constructs a {@code PageController} with the necessary services.
     *
     * @param jwtUtil The utility class for handling JSON Web Tokens.
     */
    PageController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Handles requests to the root path ("/") after SAML login.
     * Checks for the presence and validity of a "token" cookie. If a valid token
     * is found, it redirects to "/index.html". Otherwise, it still redirects to
     * "/index.html" but logs the absence or invalidity of the token.
     *
     * @param principal the authenticated SAML principal (may be null depending on the flow)
     * @param request   the incoming HTTP request
     * @param response  the outgoing HTTP response
     * @return a redirect response to the frontend ("/index.html").
     */
    @RequestMapping("/")
    public ResponseEntity<?> oktaLogin(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        // If token cookie already exists, the user is already logged in
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (token != null && !token.isBlank() && jwtUtil.validateToken(token)) {
                        logger.info("oktaLogin - Valid JWT cookie found. Redirecting to /index.html");
                        return ResponseEntity.status(HttpStatus.FOUND)
                                .header("Location", "/index.html")
                                .build();
                    } else {
                        logger.info("oktaLogin - Invalid or empty JWT token. Proceeding without redirect.");
                    }
                }
            }

        }

        // If the token cookie is missing or invalid after checking, there's nothing to do, so redirect anyway
        logger.info("oktaLogin - No valid token cookie found. Redirecting to /index.html without changes.");
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();
    }

    /**
     * Displays all cookies received in the current request.
     * Useful for debugging cookie propagation, particularly for the authentication token.
     * Accessible via the {@code /api/debug/cookies} endpoint.
     *
     * @param request the incoming HTTP request
     * @return A {@link ResponseEntity} containing a plain-text list of cookie names and values,
     * or a message indicating no cookies were received.
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

    /**
     * Handles requests to the "/event" path.
     * Redirects the client to the static "/event.html" page.
     *
     * @return A {@link ResponseEntity} with HTTP 302 (Found) and the Location header set to "/event.html".
     */
    @RequestMapping("/event")
    public ResponseEntity<?> eventPage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/event.html")
                .build();
    }

    /**
     * Handles requests to the "/event/event_creation" path.
     * Redirects the client to the static "/event/eventcreation.html" page.
     *
     * @return A {@link ResponseEntity} with HTTP 302 (Found) and the Location header set to "/event/eventcreation.html".
     */
    @RequestMapping("/event/event_creation")
    public ResponseEntity<?> eventCreationPage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/event/eventcreation.html")
                .build();
    }
}