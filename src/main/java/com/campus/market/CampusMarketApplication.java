package com.campus.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CampusMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMarketApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Campus Circular Market is running!");
        System.out.println(" Open your browser at: http://localhost:8080");
        System.out.println(" Admin login: admin@campus.edu / admin123");
        System.out.println(" Student login: student@campus.edu / student123");
        System.out.println("=================================================");
    }
}

