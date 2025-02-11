package com.aston.userservice.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO класc
 *
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
