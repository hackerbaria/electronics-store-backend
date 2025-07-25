package com.altech.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ElectronicsStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElectronicsStoreApplication.class, args);
    }
}
