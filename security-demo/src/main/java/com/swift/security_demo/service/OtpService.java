package com.swift.security_demo.service;

import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpTypeEnum;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.service.Impl.AuthServiceImpl;


public interface OtpService {
    ApiResponse verifyOtp(OtpVerificationRequest otpVerificationRequest);
    ApiResponse resendOtp();

    String generateAndSaveOtp(UserEntity userEntity , OtpTypeEnum otpType);
    void verify(OtpEntity otpEntityDb, String otpCode);
}
