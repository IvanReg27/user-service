package com.aston.userservice.config.security;

import com.aston.userservice.security.CustomAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Primary
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(CustomAuthenticationManager customAuthenticationManager) {
        return customAuthenticationManager;
    }
}
