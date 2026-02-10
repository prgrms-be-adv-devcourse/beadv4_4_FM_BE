package com.mossy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CashApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashApplication.class, args);
    }
}