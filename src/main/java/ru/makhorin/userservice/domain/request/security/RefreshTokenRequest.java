package ru.makhorin.userservice.domain.request.security;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO класc для обновления пользователю ранее выданного токена (аутентификация)
 *
 */
@Getter
@Setter
public class RefreshTokenRequest {
    private String refreshToken;
}
