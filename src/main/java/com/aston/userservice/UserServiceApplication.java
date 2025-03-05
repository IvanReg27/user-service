package com.aston.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableAspectJAutoProxy
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
