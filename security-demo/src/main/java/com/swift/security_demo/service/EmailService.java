package com.swift.security_demo.service;

public interface EmailService {
     void sendOtpEmail(String to, String username, String otp);

    //private void sendHtmlEmail(String to, String subject, String htmlBody);
}
