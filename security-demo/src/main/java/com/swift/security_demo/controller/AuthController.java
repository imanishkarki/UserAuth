package com.swift.security_demo.controller;
import com.swift.security_demo.payload.request.AccessTokenRequest;
import com.swift.security_demo.payload.request.LoginRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.service.AuthService;
import com.swift.security_demo.service.Impl.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest  loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/access/token")
    public ResponseEntity<ApiResponse> regenerateAccessToken(@RequestBody AccessTokenRequest accessTokenRequest) {
        return ResponseEntity.ok(jwtService.regenerateAccessToken(accessTokenRequest));

    }
}
