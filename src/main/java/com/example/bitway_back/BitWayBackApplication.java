package com.example.bitway_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "com.example.bitway_back.repository",
        entityManagerFactoryRef = "entityManagerFactory"
)
@EnableTransactionManagement
public class BitWayBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(BitWayBackApplication.class, args);
    }
}