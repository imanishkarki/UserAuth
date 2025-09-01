package com.swift.security_demo.service.Impl;
import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpStatusEnum;
import com.swift.security_demo.enums.UserAccountStatusEnum;
import com.swift.security_demo.exception.AllException;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.payload.response.OtpResendResponse;
import com.swift.security_demo.payload.response.OtpVerificationResponse;
import com.swift.security_demo.repository.OtpRepository;
import com.swift.security_demo.repository.UserRepository;
import com.swift.security_demo.service.EmailService;
import com.swift.security_demo.service.OtpService;
import com.swift.security_demo.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    public ApiResponse verifyOtp(OtpVerificationRequest otpVerificationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) authentication.getPrincipal();
        Long id = user.getId();
        UserEntity userEntity = userRepository.findById(id).get();

        OtpEntity otpEntityDb = otpRepository.findTopByUserEntity_IdOrderByCreatedAtDesc(id).get();

        String otpFromDb = otpEntityDb.getOtp();
        String otpCode = otpVerificationRequest.getOtp();
        if  (userEntity.getStatus().equals(UserAccountStatusEnum.VERIFIED)) {
            throw  AllException.builder()
                    .code("AVU00")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (isOtpExpired(otpEntityDb)) {
            otpEntityDb.setStatus(OtpStatusEnum.EXPIRED);
            otpRepository.save(otpEntityDb);
            throw AllException.builder()
                    .code("OE00")
                    .status(HttpStatus.GONE)
                    .build();
        } else if (otpCode.equals(otpFromDb) && otpEntityDb.getStatus().equals(OtpStatusEnum.SENT)) {
            otpEntityDb.setStatus(OtpStatusEnum.USED);
            userService.verifyUser(id);
            otpRepository.save(otpEntityDb);
            OtpVerificationResponse otpVerificationResponse = OtpVerificationResponse.builder()
                    .id(id)
                    .username(userEntity.getUsername())
                    .build();
            return new ApiResponse(otpVerificationResponse, true, "OTP verification success!" );
        } else {
            throw AllException.builder()
                    .code("OE01")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }


    private Boolean isOtpExpired(OtpEntity otpEntity) {
        if (otpEntity.getExpiresAt().isBefore(otpEntity.getExpiresAt())) {
            return true;
        }
        return false;
    }


    public ApiResponse resendOtp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();

        if (userEntity.getStatus().equals(UserAccountStatusEnum.VERIFIED)){
            throw AllException.builder()
                    .code("AVU00")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        String otp = ApplicationUtils.generateOTPCode();

        OtpEntity newOtpEntity = OtpEntity.builder()
                .otp(otp)
                .status(OtpStatusEnum.SENT)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1))

                .userEntity(userEntity).build();


        otpRepository.save(newOtpEntity);
       // emailService.sendOtpEmail(userEntity.getEmail(),userEntity.getUsername(),otp );

         OtpResendResponse otpResend = OtpResendResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .otp(otp).build();
        return new ApiResponse(otpResend , true, "OTP is resent");
    }
}

