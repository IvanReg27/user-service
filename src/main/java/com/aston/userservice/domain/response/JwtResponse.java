package com.aston.userservice.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "Response for access token and refresh token")
public record JwtResponse(String accessToken, String refreshToken) {

    @Builder
    public JwtResponse {
    }
}
