package com.aston.userservice.domain.request.security;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO класc для принятия логина и пароля (аутентификация)
 *
 */
@Getter
@Setter
public class AuthRequest {
    private String login;
    private String password;
}
