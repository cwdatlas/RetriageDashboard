package com.retriage.retriage.controller;

// Imports the Controller annotation from Spring Framework.
// This annotation marks a class as a controller, which handles web requests.
import org.springframework.stereotype.Controller;

// Imports the GetMapping annotation. This annotation maps
// HTTP GET requests to specific handler methods.
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Maps GET requests to the "/dashboard" URL path to the dashboard() method.
    // When a user accesses /dashboard, this method will be executed.
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard.html"; // This will look for dashboard.html in the resources/static folder
    }

    // Maps GET requests to the "/login" URL path to the login() method.
    // When a user accesses /dashboard, this method will be executed.
    @GetMapping("/login")
    public String login() {
        return "login.html"; // This will look for login.html in the resources/static folder
    }

    // Maps GET requests to the "/" URL path to the index() method.
    // When a user accesses /dashboard, this method will be executed.
    @GetMapping("/")
    public String index() {
        return "index.html"; // This will look for index.html in the resources/static folder
    }

}