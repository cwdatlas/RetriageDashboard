package com.retriage.retriage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableScheduling
public class RetriageApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetriageApplication.class, args);
    }
}
