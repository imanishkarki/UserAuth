package com.swift.security_demo.service.Impl;
import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpStatusEnum;
import com.swift.security_demo.exception.AllException;
import com.swift.security_demo.payload.request.OtpVerificationRequest;
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

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final UserRepository userRepository;


    public String verifyOtp(OtpVerificationRequest otpVerificationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) authentication.getPrincipal();
        Long id = user.getId();

        OtpEntity otpEntityDb = otpRepository.findTopByUserEntity_IdOrderByCreatedAtDesc(id).get();

        String otpFromDb = otpEntityDb.getOtp();
        String otpCode = otpVerificationRequest.getOtp();

        if (isOtpExpired(otpEntityDb)) {
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
            return "User verified";
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

    public String resendOtp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();

        String otp = ApplicationUtils.generateOTPCode();
        Long id = userEntity.getId();
        OtpEntity otpEntity = otpRepository.findTopByUserEntity_IdOrderByCreatedAtDesc(id).get();
        otpEntity.setOtp(otp);
        otpEntity.setStatus(OtpStatusEnum.SENT);
        otpRepository.save(otpEntity);
       // emailService.sendOtpEmail(userEntity.getEmail(),userEntity.getUsername(),otp );
        return "The new otp is: " + otp;
    }

//    private Boolean isOtpExpired(OtpEntity otpEntityDb) {
//        if(otpEntityDb.getStatus().equals(OtpStatusEnum.EXPIRED)) {
//            return true;
//        }
//        return false;
//    }

//    @Scheduled(fixedRate = 60000) // every 1 minute
//    private void expireOtps(){
//        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(1);
//
//        List<OtpEntity> expiredOtps = otpRepository.findByStatusAndCreatedAtBefore(OtpStatusEnum.SENT,expiryTime);
//
//        for (OtpEntity otpEntity : expiredOtps) {
//            otpEntity.setStatus(OtpStatusEnum.EXPIRED);
//        }
//
//        otpRepository.saveAll(expiredOtps);
//    }
}

