package com.swift.security_demo.controller;
import com.swift.security_demo.payload.request.*;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.service.AuthService;
import com.swift.security_demo.service.Impl.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth APIs",description = "APIs to login, generate tokens, update and reset passwords")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")

    @Operation(summary = "Authenticates the user and returns a JWT access and refresh token.")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest  loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/access/token")
    @Operation(summary = "Generates a new access token using a valid refresh token")
    public ResponseEntity<ApiResponse> regenerateAccessToken(@RequestBody AccessTokenRequest accessTokenRequest) {
        return ResponseEntity.ok(jwtService.regenerateAccessToken(accessTokenRequest));

    }

    @PutMapping("/update/password")
    @Operation(summary = "Allows an authenticated user to update their account password.")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return ResponseEntity.ok(authService.updatePassword(updatePasswordRequest));
    }

    @GetMapping("/forgot/password")
    @Operation(summary = "Initiates the password reset process by sending an OTP to the user’s email.")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordRequest));
    }

    @PutMapping("/verify/password/otp")
    @Operation(summary = "Verifies the OTP sent for password reset before allowing a new password to be set.")
    public ResponseEntity<ApiResponse> verifyPasswordOtp(@RequestBody PasswordOtpVerifyRequest passwordOtpVerifyRequest){
        return ResponseEntity.ok(authService.verifyPasswordOtp(passwordOtpVerifyRequest));
    }

}
