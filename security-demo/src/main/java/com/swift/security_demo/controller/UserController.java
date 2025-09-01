package com.swift.security_demo.controller;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
import com.swift.security_demo.payload.request.SignupRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.service.Impl.OtpServiceImpl;
import com.swift.security_demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final OtpServiceImpl otpService;
    public UserController(UserService userService, OtpServiceImpl otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse>  register(@Valid @RequestBody SignupRequest signupRequest) {
        //userService.register(signupRequest);
        return ResponseEntity.ok(userService.register(signupRequest));
    }

//    @GetMapping("/otp")
//    public String generateOtp(){
//        return  otpService.generateOtp(6);
//    }

    @PutMapping("/verify/otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        return ResponseEntity.ok(otpService.verifyOtp( otpVerificationRequest));
    }

    @GetMapping("/resend/otp")
    public ResponseEntity<ApiResponse> resendOtp() {
        return  ResponseEntity.ok(otpService.resendOtp());

    }








    //------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/test")
    public String test(){
        return userService.test();
    }

    @GetMapping("/public/test")
    public String publicTest(){
        return "Public Test is working!!";
    }

    @GetMapping("/public")
    public String test2(){
        return "This is public Api";
    }

    @PreAuthorize("hasRole('VERIFIED')")
    @GetMapping("/private")
    public String test3(){
        return "This is private Api. The user is VERIFIED.";
    }
}
