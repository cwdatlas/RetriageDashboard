package com.retriage.retriage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * The main entry point for the Retriage Spring Boot application.
 * This class bootstraps the application, enabling auto-configuration, component scanning,
 * scheduled task execution via {@link EnableScheduling}, and method-level security via {@link EnableMethodSecurity}.
 */
@SpringBootApplication
@EnableScheduling
@EnableMethodSecurity // Enables Spring Security's method-level security annotations (like @PreAuthorize)
public class RetriageApplication {

    /**
     * Default constructor for the main RetriageApplication class.
     * Primarily used internally by the Spring Boot framework upon startup.
     */
    public RetriageApplication() {
        super();
    }

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(RetriageApplication.class, args);
    }
}