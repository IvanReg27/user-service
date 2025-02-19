package com.aston.userservice.controller.impl;


import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.request.security.AuthRequest;
import com.aston.userservice.domain.request.security.RefreshTokenRequest;
import com.aston.userservice.domain.response.security.AuthResponse;
import com.aston.userservice.service.impl.UserServiceImpl;
import com.aston.userservice.util.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceImpl userServiceImpl;

    @Loggable
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

            UserDetails userDetails = userServiceImpl.loadUserByUsername(request.getLogin());
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
        }
    }

    @Loggable
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Неверный или просроченный рефреш token");
        }

        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = userServiceImpl.loadUserByUsername(username);

        String newAccessToken = jwtTokenUtil.generateToken(userDetails);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }
}
