package com.library.usermanagementsystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {

        String verificationLink =
                "http://localhost:8080/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("letiendungltw@gmail.com");
        message.setTo(to);
        message.setSubject("Verify your email");
        message.setText(
                "Please click the link below to verify your email:\n\n"
                        + verificationLink
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(
            String to,
            String token
    ) {

        String resetLink =
                "http://localhost:8080/auth/reset-password?token="
                        + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom("letiendungltw@gmail.com");
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText(
                "Please click the link below to reset your password:\n\n"
                        + resetLink
        );

        mailSender.send(message);
    }

}