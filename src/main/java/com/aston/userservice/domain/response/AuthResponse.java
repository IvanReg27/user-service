package com.aston.userservice.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO класc для возврата токена, после принятия логина и пароля
 *
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
