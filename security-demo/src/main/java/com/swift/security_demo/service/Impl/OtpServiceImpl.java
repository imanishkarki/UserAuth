package com.swift.security_demo.service.Impl;
import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpStatusEnum;
import com.swift.security_demo.enums.OtpTypeEnum;
import com.swift.security_demo.enums.UserAccountStatusEnum;
import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
import com.swift.security_demo.payload.response.ApiResponse;
import com.swift.security_demo.payload.response.OtpResponse;
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
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
        OtpTypeEnum otpTypeEnum = otpEntityDb.getOtpType();
        //   String otpFromDb = otpEntityDb.getOtp();
        String otpCode = otpVerificationRequest.getOtp();
        if  (userEntity.getStatus().equals(UserAccountStatusEnum.VERIFIED)) {
            throw  BaseException.builder()
                    .code("AVU00")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (!otpTypeEnum.equals(OtpTypeEnum.ACCOUNT_VERIFICATION_OTP)) {
            throw BaseException.builder()
                    .code("OE02")
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .build();
        } else{
            verify(otpEntityDb,  otpCode);
             userService.verifyUser(id);

        }
        OtpVerificationResponse otpVerificationResponse = OtpVerificationResponse.builder()
                .id(id)
                .username(userEntity.getUsername())
               .build();
             return new ApiResponse(otpVerificationResponse, true, "OTP verification success!" );
    }

    public void  verify(OtpEntity otpEntityDb, String otpCode) {
            String otpFromDb = otpEntityDb.getOtp();
     if (isOtpExpired(otpEntityDb)) {
        otpEntityDb.setStatus(OtpStatusEnum.EXPIRED);
        otpRepository.save(otpEntityDb);
        throw BaseException.builder()
                .code("OE00")
                .status(HttpStatus.GONE)
                .build();
    } else if (otpCode.equals(otpFromDb) && otpEntityDb.getStatus().equals(OtpStatusEnum.SENT)) {

        otpEntityDb.setStatus(OtpStatusEnum.EXPIRED);
       // userService.verifyUser(id);
        otpRepository.save(otpEntityDb);
    } else {
        throw BaseException.builder()
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
        Long id = userEntity.getId();
        //OtpEntity otpEntityDb = otpRepository.findById(id).get();

        if (userEntity.getStatus().equals(UserAccountStatusEnum.VERIFIED)){
            throw BaseException.builder()
                    .code("AVU00")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        OtpTypeEnum otpType = OtpTypeEnum.ACCOUNT_VERIFICATION_OTP;
        String otp = generateAndSaveOtp(userEntity, otpType );
         OtpResponse otpResend = OtpResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .otp(otp).build();
        return new ApiResponse(otpResend , true, "OTP is resent");
    }

    public String generateAndSaveOtp(UserEntity userEntity, OtpTypeEnum otpType) {
            List<OtpEntity> otpEntities = otpRepository.findByUserEntityId(userEntity.getId()).get();
            for (OtpEntity otpEntity : otpEntities) {
                otpEntity.setStatus(OtpStatusEnum.EXPIRED);
            }
            otpRepository.saveAll(otpEntities);
        String otp = ApplicationUtils.generateOTPCode();
        OtpEntity newOtpEntity = OtpEntity.builder()
                .otp(otp)
                .otpType(otpType)
                .status(OtpStatusEnum.SENT)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .userEntity(userEntity).build();

        otpRepository.save(newOtpEntity);
        return otp;
        // emailService.sendOtpEmail(userEntity.getEmail(),userEntity.getUsername(),otp );
    }
}

