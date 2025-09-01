package com.swift.security_demo.service;

import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.payload.request.SignupRequest;
import com.swift.security_demo.payload.response.ApiResponse;

public interface UserService {
    ApiResponse register(SignupRequest signupRequest);

    String test();

    void verifyUser(Long id);
}
