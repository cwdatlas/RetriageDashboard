package com.retriage.retriage.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
public class TestController {

    @GetMapping("/test-exception")
    public ResponseEntity<String> triggerException() {
        throw new RuntimeException("This is a test exception to check the handler!");
    }
}