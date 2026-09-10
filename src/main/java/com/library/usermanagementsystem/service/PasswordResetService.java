package com.library.usermanagementsystem.service;

import com.library.usermanagementsystem.entity.PasswordResetToken;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.exception.PasswordResetException;
import com.library.usermanagementsystem.repository.PasswordResetTokenRepository;
import com.library.usermanagementsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordResetToken createResetToken(String email) {

        User user = userRepository
                .findAll()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() ->
                        new PasswordResetException(
                                "User with this email not found"
                        )
                );

        PasswordResetToken token =
                new PasswordResetToken();

        token.setToken(UUID.randomUUID().toString());
        token.setUserName(user.getUserName());

        token.setExpiryDate(
                new Timestamp(
                        System.currentTimeMillis()
                                + 1000L * 60 * 30
                )
        );

        return tokenRepository.save(token);
    }

    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new PasswordResetException(
                                        "Reset token not found"
                                )
                        );

        if (resetToken.getExpiryDate().before(
                new Timestamp(System.currentTimeMillis())
        )) {

            tokenRepository.delete(resetToken);

            throw new PasswordResetException(
                    "Reset token expired"
            );
        }

        User user = userRepository
                .findByUserName(resetToken.getUserName())
                .orElseThrow(() ->
                        new PasswordResetException(
                                "User not found"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

}