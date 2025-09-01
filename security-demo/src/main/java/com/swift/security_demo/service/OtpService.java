package com.swift.security_demo.service;

import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
import com.swift.security_demo.payload.response.ApiResponse;

public interface OtpService {
    ApiResponse verifyOtp(OtpVerificationRequest otpVerificationRequest);
    ApiResponse resendOtp();
}
