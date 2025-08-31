package com.swift.security_demo.service;

import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.payload.request.OtpVerificationRequest;

public interface OtpService {
    String verifyOtp(OtpVerificationRequest otpVerificationRequest);
    String resendOtp();
}
