package com.aston.userservice.controller.impl;

import com.aston.userservice.domain.request.AuthRequest;
import com.aston.userservice.domain.request.RefreshJwtRequest;
import com.aston.userservice.domain.response.JwtResponse;
import com.aston.userservice.domain.response.SimpleMessage;
import com.aston.userservice.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/frontend-practice/auth")
@RequiredArgsConstructor
@Tag(name = "Tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "auth-1: get accessToken(lives 1 seconds) and refreshToken(lives 60 seconds))",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success auth",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Incorrect login/password",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleMessage.class))),
            }
    )
    public ResponseEntity<JwtResponse> authAndGetToken(
            @Parameter(description = "login and password request for auth (default admin/admin)")
            @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authAndGetToken(authRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "auth-2: refresh tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Incorrect refresh token",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleMessage.class))),
            }
    )
    public ResponseEntity<JwtResponse> getNewAccessToken(
            @Parameter(description = "refresh token request")
            @RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
