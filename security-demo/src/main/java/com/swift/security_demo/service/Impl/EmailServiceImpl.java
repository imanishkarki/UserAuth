package com.swift.security_demo.service.Impl;

import com.swift.security_demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

//    @Async
//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Email verification");
//        message.setText("Your email verification OTP code is: "+ body);
//        mailSender.send(message);
//    }

    @Async  // Optional: for async sending
    public void sendOtpEmail(String to, String username, String otp) {

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("otp", otp);

        String htmlContent = templateEngine.process("EmailTemplate", context);

        sendHtmlEmail(to, "Your OTP Code", htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Handle exception (logging, retry, etc.)
            e.printStackTrace();
        }
    }
}
