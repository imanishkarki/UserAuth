package com.swift.security_demo.payload.response;

import lombok.*;

@Getter
@Setter
@Builder

public class AccessTokenResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
}
