package com.swift.security_demo.service;

import com.swift.security_demo.payload.request.LoginRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
}
