package com.max.productivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.max.productivity")
public class ProductivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductivityApplication.class, args);
    }
}

