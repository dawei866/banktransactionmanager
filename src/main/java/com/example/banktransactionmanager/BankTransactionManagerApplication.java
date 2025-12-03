package com.example.banktransactionmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BankTransactionManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankTransactionManagerApplication.class, args);
    }
}