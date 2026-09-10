package com.library.usermanagementsystem.controller;

import com.library.usermanagementsystem.dto.LoginRequest;
import com.library.usermanagementsystem.dto.LoginResponse;
import com.library.usermanagementsystem.dto.RefreshTokenRequest;
import com.library.usermanagementsystem.entity.RefreshToken;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.repository.UserRepository;
import com.library.usermanagementsystem.service.JwtService;
import com.library.usermanagementsystem.service.RefreshTokenService;
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

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
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
    public ResponseEntity<String> refresh(
            @RequestBody RefreshTokenRequest request
    ) {

        String newAccessToken =
                refreshTokenService.refreshAccessToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequest request
    ) {

        refreshTokenService.deleteRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.noContent().build();
    }

}