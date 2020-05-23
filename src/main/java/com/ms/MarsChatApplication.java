package com.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.ms")
@EnableScheduling
public class MarsChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarsChatApplication.class, args);
    }
}
