package com.retriage.retriage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RetriageApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetriageApplication.class, args);
    }
    //TEST TO SEE IF THE MERGE WORKED
}
