package com.swift.security_demo.service;

import jakarta.mail.MessagingException;

public interface EmailService {
     void sendOtpEmail(String to, String username, String otp) throws MessagingException;

    //private void sendHtmlEmail(String to, String subject, String htmlBody);
}
