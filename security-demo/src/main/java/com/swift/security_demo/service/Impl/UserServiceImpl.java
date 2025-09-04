package com.swift.security_demo.service.Impl;
import com.swift.security_demo.config.CharArrayToStringConverter;
import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpStatusEnum;
import com.swift.security_demo.enums.OtpTypeEnum;
import com.swift.security_demo.enums.UserAccountStatusEnum;
import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.payload.request.SignupRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.payload.response.SignupResponse;
import com.swift.security_demo.repository.OtpRepository;
import com.swift.security_demo.repository.UserRepository;
import com.swift.security_demo.service.EmailService;
import com.swift.security_demo.service.UserService;
import com.swift.security_demo.utils.ApplicationUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final CharArrayToStringConverter charArrayToStringConverter;

    @Override
    public ApiResponse register(SignupRequest signupRequest) throws MessagingException, BaseException {
        String otpNum = ApplicationUtils.generateOTPCode();

        Optional<UserEntity> existingUser = userRepository.findByUsername(signupRequest.getUsername());

        if (existingUser.isPresent() && existingUser.get().getStatus().equals(UserAccountStatusEnum.VERIFIED)) {
            throw BaseException.builder()
                    .code("AE00")
                    .status(HttpStatus.BAD_REQUEST)
            .build();
        } else if (existingUser.isPresent() && existingUser.get().getStatus().equals(UserAccountStatusEnum.PENDING)) {
            throw BaseException.builder()
                    .code("AE01")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else {

                    String password = charArrayToStringConverter.convert(signupRequest.getPassword());
                    String encodedPassword = passwordEncoder.encode(password);
            UserEntity user = UserEntity.builder()
                    .name(signupRequest.getName())
                    .email(signupRequest.getEmail())
                    .password(encodedPassword)
                    .phoneNumber(signupRequest.getPhoneNumber())
                    .username(signupRequest.getUsername())
                    .createdAt(LocalDateTime.now())
                    .status(UserAccountStatusEnum.PENDING)
                    .build();

            // Create OTP and link it to user
            OtpEntity otpEntity = getOtpEntity(otpNum, user);
            user.setOtpEntity(List.of(otpEntity));
           // emailService.sendOtpEmail(signupRequest.getEmail(), signupRequest.getUsername(), otpNum );

            userRepository.save(user);
            otpRepository.save(otpEntity);
            SignupResponse signupResponse = SignupResponse.builder()
                    .username(signupRequest.getUsername())
                    .otp(otpNum)
                    .status(UserAccountStatusEnum.PENDING.toString())
                    .build();
            return new ApiResponse(signupResponse, true, "User successfully created");

        }
    }

    private OtpEntity getOtpEntity(String otpNum, UserEntity user) {
        OtpEntity otpEntity = OtpEntity.builder()
                .otp(otpNum)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .status(OtpStatusEnum.SENT)
                .otpType(OtpTypeEnum.ACCOUNT_VERIFICATION_OTP)
                .userEntity(user)  //  SET the user here
                .build();
        return otpEntity;
    }

    public void  verifyUser(Long id) {
        UserEntity user = userRepository.findById(id).get();
        user.setStatus(UserAccountStatusEnum.VERIFIED);
        userRepository.save(user);
    }
}
