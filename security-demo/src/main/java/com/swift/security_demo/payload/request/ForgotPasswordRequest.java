package com.swift.security_demo.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ForgotPasswordRequest {
    private String username;
   // private String newPassword;
    //private String otp;
}
