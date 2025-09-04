package com.swift.security_demo.service;

import com.swift.security_demo.payload.request.ForgotPasswordRequest;
import com.swift.security_demo.payload.request.LoginRequest;
import com.swift.security_demo.payload.request.PasswordOtpVerifyRequest;
import com.swift.security_demo.payload.request.UpdatePasswordRequest;
import com.swift.security_demo.payload.response.ApiResponse;

public interface AuthService {
    ApiResponse login(LoginRequest loginRequest);

    ApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);

    ApiResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    ApiResponse verifyPasswordOtp(PasswordOtpVerifyRequest passwordOtpVerifyRequest);
}
