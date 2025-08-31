package com.swift.security_demo.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OtpVerificationResponse {
    private Long id;
    private  String username;
}
