package com.library.usermanagementsystem.controller;

import com.library.usermanagementsystem.dto.*;
import com.library.usermanagementsystem.entity.PasswordResetToken;
import com.library.usermanagementsystem.entity.RefreshToken;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.repository.UserRepository;
import com.library.usermanagementsystem.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService,
            EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByUserName(request.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(
                user.getUserName(),
                user.getRole()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getUserName()
                );

        LoginResponse response = new LoginResponse(
                token,
                refreshToken.getToken(),
                user.getUserName(),
                user.getRole()
        );

        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {

        RefreshToken newRefreshToken =
                refreshTokenService.refreshRefreshToken(
                        request.getRefreshToken()
                );

        String newAccessToken =
                jwtService.generateToken(
                        newRefreshToken.getUserName(),
                        userRepository
                                .findByUserName(newRefreshToken.getUserName())
                                .orElseThrow(() ->
                                        new UsernameNotFoundException("User not found")
                                )
                                .getRole()
                );

        RefreshTokenResponse response =
                new RefreshTokenResponse(
                        newAccessToken,
                        newRefreshToken.getToken()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {

        refreshTokenService.deleteRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        PasswordResetToken resetToken =
                passwordResetService.createResetToken(
                        request.getEmail()
                );

        emailService.sendPasswordResetEmail(
                request.getEmail(),
                resetToken.getToken()
        );

        return ResponseEntity.ok(
                "Password reset email sent"
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password reset successfully"
        );
    }

}