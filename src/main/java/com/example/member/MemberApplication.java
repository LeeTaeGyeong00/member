package com.example.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }

}
