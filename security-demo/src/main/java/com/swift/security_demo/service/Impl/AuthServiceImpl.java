package com.swift.security_demo.service.Impl;

import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.UserAccountStatusEnum;
import com.swift.security_demo.exception.AllException;
import com.swift.security_demo.payload.request.LoginRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.payload.response.LoginResponse;
import com.swift.security_demo.repository.UserRepository;
import com.swift.security_demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public ApiResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Load the authenticated user
        UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (authenticate.isAuthenticated())
        {
            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            //return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
            LoginResponse loginResponse = LoginResponse.builder()
                    .username(user.getUsername())
                    .status(user.getStatus().toString())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return new ApiResponse (loginResponse, true,"Login Success");
        }else{
            throw AllException.builder()
                    .code("IDE03")
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }
}
