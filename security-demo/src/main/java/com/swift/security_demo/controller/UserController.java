package com.swift.security_demo.controller;
import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
import com.swift.security_demo.payload.request.SignupRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.service.Impl.OtpServiceImpl;
import com.swift.security_demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User APIs", description = "APIs to create the user, send OTP and access other................")
public class UserController {

    private final UserService userService;
    private final OtpServiceImpl otpService;
    public UserController(UserService userService, OtpServiceImpl otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Registers a new user with email OTP verification.")
    public ResponseEntity<ApiResponse>  register(@Valid @RequestBody SignupRequest signupRequest) throws MessagingException, BaseException {
        return ResponseEntity.ok(userService.register(signupRequest));
    }


    @PutMapping("/verify/otp")
    @Operation(summary = "Verifies the OTP sent to the user's email during signup.")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        return ResponseEntity.ok(otpService.verifyOtp( otpVerificationRequest));
    }

    @GetMapping("/resend/otp")
    @Operation(summary = "Resends the OTP to the user's email for verification")
    public ResponseEntity<ApiResponse> resendOtp() {
        return  ResponseEntity.ok(otpService.resendOtp());

    }








    //------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/public")
    @Operation(summary = "A public API endpoint accessible with both VERIFIED & UNVERIFIED user, but need to be logged in.")
    public String test2(){
        return "This is public Api";
    }

    @PreAuthorize("hasRole('VERIFIED')")
    @GetMapping("/private")
    @Operation(summary = "A protected API accessible only by users with VERIFIED role.")
    public String test3(){
        return "This is private Api. The user is VERIFIED.";
    }
}
