package com.aston.userservice.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    private String refreshToken;
}
