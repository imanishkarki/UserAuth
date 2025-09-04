package com.swift.security_demo.service;

import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.payload.request.SignupRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import jakarta.mail.MessagingException;

public interface UserService {
    ApiResponse register(SignupRequest signupRequest) throws MessagingException, BaseException;


    void verifyUser(Long id);
}
