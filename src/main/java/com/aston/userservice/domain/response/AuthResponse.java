package com.aston.userservice.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO класc для возврата токена, после получения валидного логина и пароля (аутентификация)
 *
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
