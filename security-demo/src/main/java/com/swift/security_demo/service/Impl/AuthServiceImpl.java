package com.swift.security_demo.service.Impl;

import com.swift.security_demo.config.CharArrayToStringConverter;
import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpTypeEnum;
import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.payload.request.ForgotPasswordRequest;
import com.swift.security_demo.payload.request.LoginRequest;
import com.swift.security_demo.payload.request.PasswordOtpVerifyRequest;
import com.swift.security_demo.payload.request.UpdatePasswordRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.payload.response.LoginResponse;
import com.swift.security_demo.payload.response.PasswordOtpVerifyResponse;
import com.swift.security_demo.payload.response.UpdatePasswordResponse;
import com.swift.security_demo.repository.OtpRepository;
import com.swift.security_demo.repository.UserRepository;
import com.swift.security_demo.service.AuthService;
import com.swift.security_demo.service.EmailService;
import com.swift.security_demo.service.OtpService;
import com.swift.security_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CharArrayToStringConverter charArrayToStringConverter;
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public enum Caller{
        RESENT,
        FORGOT_PASSWORD
    }

    @Override
    public ApiResponse login(LoginRequest loginRequest) {

        try {
             authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            charArrayToStringConverter.convert(loginRequest.getPassword())
                    )
            );

            // Fetch user (optional if already loaded by authentication)
            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> BaseException.builder()
                            .code("IDE00")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
                    );

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            LoginResponse loginResponse = LoginResponse.builder()
                    .username(user.getUsername())
                    .status(user.getStatus().toString())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return new ApiResponse(loginResponse, true, "Login Success");

        } catch (BadCredentialsException ex) {
            // Password is wrong
            throw BaseException.builder()
                    .code("IDE01")
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();

        }


//        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//        // Load the authenticated user
//        UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        if (authenticate.isAuthenticated())
//        {
//            // Generate tokens
//            String accessToken = jwtService.generateAccessToken(user.getUsername());
//            String refreshToken = jwtService.generateRefreshToken(user.getUsername());
//
//            //return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
//            LoginResponse loginResponse = LoginResponse.builder()
//                    .username(user.getUsername())
//                    .status(user.getStatus().toString())
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//            return new ApiResponse (loginResponse, true,"Login Success");
//        }else{
//            throw AllException.builder()
//                    .code("IDE03")
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .build();
//        }
    }

    @Override
    public ApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) authentication.getPrincipal();
     //   Long id = user.getId();

        String passwordDb =user.getPassword();
        if (passwordEncoder.matches(passwordDb,passwordEncoder.encode(charArrayToStringConverter.convert(updatePasswordRequest.getOldPassword())))) {
            throw BaseException.builder()
                    .code("IDE01") //code
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        } else {
            user.setPassword(passwordEncoder.encode(charArrayToStringConverter.convert(updatePasswordRequest.getNewPassword())));
            userRepository.save(user);

        }
        UpdatePasswordResponse updatePasswordResponse = UpdatePasswordResponse.builder()
                .username(user.getUsername())
                .newPassword(charArrayToStringConverter.convert(updatePasswordRequest.getNewPassword()))
                .build();
        return new ApiResponse(updatePasswordResponse, true, "Update Password Success");
    }

    @Override
    public ApiResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Optional<UserEntity> user = userRepository.findByUsername(forgotPasswordRequest.getUsername());
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            //ApplicationUtils.generateOTPCode();
            //emailservice
            OtpTypeEnum otpType = OtpTypeEnum.FORGOT_PASSWORD_OTP;
             String otp = otpService.generateAndSaveOtp(userEntity, otpType);

             return new  ApiResponse(otp , true, "OTP is sent to reset your password");
//expire old otp
        } else {
            throw BaseException.builder()
                    .code("IDE04")
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @Override
    public ApiResponse verifyPasswordOtp(PasswordOtpVerifyRequest passwordOtpVerifyRequest) {
        Optional <UserEntity> userEntity = userRepository.findByUsername(passwordOtpVerifyRequest.getUsername());
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            Long id = user.getId();
            OtpEntity otpEntity = otpRepository.findTopByUserEntity_IdOrderByCreatedAtDesc(id).get();
            if (!otpEntity.getOtpType().equals(OtpTypeEnum.FORGOT_PASSWORD_OTP)) {
                throw BaseException.builder()
                        .code("OE02")
                        .status(HttpStatus.NOT_ACCEPTABLE)
                        .build();
            }
          //  String otpDb = otpRepository.findTopByUserEntity_IdOrderByCreatedAtDesc(id).get().getOtp();
            String otp = passwordOtpVerifyRequest.getOtp();
            otpService.verify(otpEntity, otp);
            String newPassword = passwordEncoder.encode(charArrayToStringConverter.convert(passwordOtpVerifyRequest.getNewPassword()));
            user.setPassword(newPassword);
            userRepository.save(user);
            PasswordOtpVerifyResponse passwordOtpVerifyResponse = PasswordOtpVerifyResponse.builder()
                        .username(user.getUsername())
                        .build();
                return new ApiResponse(passwordOtpVerifyResponse, true, "New Password created");
        }else{
            throw  BaseException.builder()
                    .code("IDE04")
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

}
