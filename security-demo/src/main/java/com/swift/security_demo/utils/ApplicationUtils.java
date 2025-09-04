package com.swift.security_demo.utils;
import com.swift.security_demo.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.security.SecureRandom;
public class ApplicationUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    public static String generateOTPCode() {

        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = secureRandom.nextInt(10); // generates a digit between 0-9
            otp.append(digit);
        }
        return otp.toString();
    }



    public static UserEntity getCurrentUser() {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authenticate.getPrincipal();
        if (principal instanceof UserEntity userEntity) {
            return userEntity;
        } else {
            throw new RuntimeException("Authenticated user is not of type UserEntity");//
        }
//        if (principal instanceof UserEntity) {
//            return (UserEntity) principal;
//        } else {
//            // If using a custom UserDetails or returning username string only
//            throw new RuntimeException("User not authenticated");
//        }
    }
}
