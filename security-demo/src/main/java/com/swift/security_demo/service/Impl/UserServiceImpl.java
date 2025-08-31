package com.swift.security_demo.service.Impl;
import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpStatusEnum;
import com.swift.security_demo.enums.UserAccountStatusEnum;
import com.swift.security_demo.exception.AllException;
import com.swift.security_demo.payload.request.SignupRequest;
import com.swift.security_demo.repository.OtpRepository;
import com.swift.security_demo.repository.UserRepository;
import com.swift.security_demo.service.EmailService;
import com.swift.security_demo.service.OtpService;
import com.swift.security_demo.service.UserService;
import com.swift.security_demo.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String register(SignupRequest signupRequest) {
        String otpNum = ApplicationUtils.generateOTPCode();

        Optional<UserEntity> existingUser = userRepository.findByUsername(signupRequest.getUsername());

        if (existingUser.isPresent() && existingUser.get().getStatus().equals(UserAccountStatusEnum.VERIFIED)) {
            throw AllException.builder()
                    .code("AE00")
                    .status(HttpStatus.BAD_REQUEST)
            .build();
        } else if (existingUser.isPresent() && existingUser.get().getStatus().equals(UserAccountStatusEnum.PENDING)) {
            throw AllException.builder()
                    .code("AE01")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            //UserEntity user = existingUser.get();
            //OtpEntity otpEntity = getOtpEntity(otpNum, user);
            //user.setOtpEntity(List.of(otpEntity));
            //otpRepository.save(otpEntity);
            // userRepository.save(user);
            //return ("User already exists and otp is sent. Please login and resend the otp." + "\n The new otp is: "+ otpNum);
        } else {
            signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            UserEntity user = UserEntity.builder()
                    .name(signupRequest.getName())
                    .email(signupRequest.getEmail())
                    .password(signupRequest.getPassword())
                    .phoneNumber(signupRequest.getPhoneNumber())
                    .username(signupRequest.getUsername())
                    .createdAt(LocalDateTime.now())
                    .status(UserAccountStatusEnum.PENDING)
                    .build();

            // Create OTP and link it to user
            OtpEntity otpEntity = getOtpEntity(otpNum, user);
            user.setOtpEntity(List.of(otpEntity));
            userRepository.save(user);
            otpRepository.save(otpEntity);
           // emailService.sendOtpEmail(signupRequest.getEmail(), signupRequest.getUsername(), otpNum );
            return ("User is created and the otp is:  "+otpNum);
        }
    }

    private OtpEntity getOtpEntity(String otpNum, UserEntity user) {
        OtpEntity otpEntity = OtpEntity.builder()
                .otp(otpNum)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .status(OtpStatusEnum.SENT)
                .userEntity(user)  // 🔥 SET the user here
                .build();
        return otpEntity;
    }

    public String verifyUser(Long id) {
        UserEntity user = userRepository.findById(id).get();
        user.setStatus(UserAccountStatusEnum.VERIFIED);
        userRepository.save(user);
        return "Status changed";
    }


    @Override
    public String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity user = userRepository.findByUsername(userDetails.getUsername()).get();
        if (!user.getStatus().equals(UserAccountStatusEnum.VERIFIED)) {
            return "User is verified and can access this resource";
        }else{
        throw AllException.builder()
                .code("NUV00")
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        }
    }
}