package com.swift.security_demo.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter

public class AccessTokenRequest {
    @Schema(description = "Refresh token to generate the Access token")
    private String refreshToken;
}
