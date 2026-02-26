package com.pharmacare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PharmaCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmaCareApplication.class, args);
    }
}
