package com.aston.userservice.domain.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO класc
 *
 */
@Getter
@Setter
public class AuthRequest {
    private String login;
    private String password;
}
