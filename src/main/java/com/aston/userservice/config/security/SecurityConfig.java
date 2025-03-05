package com.aston.userservice.config.security;

import com.aston.userservice.security.CustomAuthenticationManager;
import com.aston.userservice.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationManager customAuthenticationManager;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomAuthenticationManager customAuthenticationManager) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customAuthenticationManager = customAuthenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(customAuthenticationManager)
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST, "/user-service/user").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-service/user/**").authenticated()
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION) // Добавляем фильтр для аутентификации
                .build();
    }
}
