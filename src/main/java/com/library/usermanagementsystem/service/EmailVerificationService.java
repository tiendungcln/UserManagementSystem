package com.library.usermanagementsystem.service;

import com.library.usermanagementsystem.entity.EmailVerificationToken;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.exception.EmailVerificationException;
import com.library.usermanagementsystem.exception.RefreshTokenException;
import com.library.usermanagementsystem.repository.EmailVerificationTokenRepository;
import com.library.usermanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public EmailVerificationToken createVerificationToken(String userName) {

        EmailVerificationToken token =
                new EmailVerificationToken();

        token.setToken(UUID.randomUUID().toString());
        token.setUserName(userName);

        token.setExpiryDate(
                new Timestamp(
                        System.currentTimeMillis()
                                + 1000L * 60 * 30
                )
        );

        return tokenRepository.save(token);
    }

    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new EmailVerificationException(
                                        "Verification token not found"
                                )
                        );

        if (verificationToken.getExpiryDate().before(
                new Timestamp(System.currentTimeMillis())
        )) {

            tokenRepository.delete(verificationToken);

            throw new EmailVerificationException(
                    "Verification token expired"
            );
        }

        User user = userRepository
                .findByUserName(verificationToken.getUserName())
                .orElseThrow(() ->
                        new EmailVerificationException(
                                "User not found"
                        )
                );

        user.setVerified(true);

        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}