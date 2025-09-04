package com.swift.security_demo.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
public class OtpResponse {
    private Long id;
    private String username;
    private String otp;
}
