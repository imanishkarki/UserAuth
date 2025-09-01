package com.swift.security_demo.payload.request;

import lombok.*;

@Builder
@Getter
@Setter

public class AccessTokenRequest {
    private String refreshToken;
}
