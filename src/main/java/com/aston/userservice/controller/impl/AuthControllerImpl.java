package com.aston.userservice.controller.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.request.security.AuthRequest;
import com.aston.userservice.domain.request.security.RefreshTokenRequest;
import com.aston.userservice.domain.response.security.AuthResponse;
import com.aston.userservice.security.CustomAuthenticationManager;
import com.aston.userservice.service.impl.UserServiceImpl;
import com.aston.userservice.util.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Контроллер, который обрабатывает вход пользователя
 * и выдает ему JWT аксес и рефреш токены
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthControllerImpl {

    private final CustomAuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceImpl userServiceImpl;

    @Loggable
    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest request) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
                ).flatMap(auth -> userServiceImpl.findByUsername(request.getLogin()))
                .map(userDetails -> new AuthResponse(
                        jwtTokenUtil.generateToken(userDetails),
                        jwtTokenUtil.generateRefreshToken(userDetails)
                ))
                .onErrorResume(BadCredentialsException.class, e ->
                        Mono.error(new RuntimeException("Неверный логин или пароль", e))
                );
    }

    @Loggable
    @PostMapping("/refresh-token")
    public Mono<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return Mono.just(request.getRefreshToken())
                .filter(jwtTokenUtil::validateRefreshToken)
                .switchIfEmpty(Mono.error(new RuntimeException("Неверный или просроченный рефреш token")))
                .map(jwtTokenUtil::extractUsername)
                .flatMap(userServiceImpl::findByUsername)
                .map(userDetails -> new AuthResponse(
                        jwtTokenUtil.generateToken(userDetails),
                        jwtTokenUtil.generateRefreshToken(userDetails)
                ));
    }
}
