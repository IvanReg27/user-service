package com.aston.userservice.security;

import com.aston.userservice.util.security.JwtTokenUtil;
import com.aston.userservice.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Реактивный фильтр для аутентификации пользователя по JWT токену
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements WebFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceImpl userServiceImpl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        return Mono.just(token)
                .map(jwtTokenUtil::extractUsername)
                .flatMap(userServiceImpl::findByUsername)
                .filter(userDetails -> jwtTokenUtil.validateToken(token, userDetails))
                .map(this::createAuthentication)
                .flatMap(auth -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just((SecurityContext) auth))))
                .onErrorResume(e -> {
                    log.error("Ошибка при валидации JWT: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private Mono<SecurityContext> createAuthentication(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return Mono.just(new SecurityContextImpl(authToken));
    }
}
