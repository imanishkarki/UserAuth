package com.swift.security_demo.controller;
import com.swift.security_demo.payload.request.LoginRequest;
import com.swift.security_demo.service.AuthService;
import com.swift.security_demo.service.Impl.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest  loginRequest){

        return authService.login(loginRequest);
    }

    @PostMapping("/access/token")
    public String regenerateAccessToken(@RequestParam String refreshToken) {
        if  (!jwtService.isTokenExpired(refreshToken)) {
            return "expired token";// exception handling left
        }
        String username = jwtService.extractUsername(refreshToken);
        String newAcessToken = jwtService.generateAccessToken(username);
        return "new access token: " + newAcessToken;
    }
}
