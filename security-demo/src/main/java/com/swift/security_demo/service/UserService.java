package com.swift.security_demo.service;

import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.payload.request.SignupRequest;

public interface UserService {
    String register(SignupRequest signupRequest);

    String test();

    String verifyUser(Long id);
}
