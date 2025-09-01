package com.swift.security_demo.service;

import com.swift.security_demo.payload.request.LoginRequest;
import com.swift.security_demo.payload.response.ApiResponse;

public interface AuthService {
    ApiResponse login(LoginRequest loginRequest);
}
