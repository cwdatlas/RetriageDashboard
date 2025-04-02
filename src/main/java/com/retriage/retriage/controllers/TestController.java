package com.retriage.retriage.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // Simple endpoint to check authentication and roles
    @GetMapping("/secured")
    @PreAuthorize("hasRole('Director')") // set the controller to a specific authorization
    public ResponseEntity<String> securedEndpoint() {
        System.out.println("Secured endpoint");
        return ResponseEntity.ok("You have access to this secured endpoint!");
    }

    // Public endpoint (to test accessibility without authentication)
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint accessible without authentication.");
    }
}
