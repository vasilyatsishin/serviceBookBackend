package com.example.serviceBookBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ServiceBookBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBookBackendApplication.class, args);
    }

}
