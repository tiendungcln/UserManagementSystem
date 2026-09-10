package com.library.usermanagementsystem.controller;

import com.library.usermanagementsystem.dto.LoginRequest;
import com.library.usermanagementsystem.dto.LoginResponse;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.repository.UserRepository;
import com.library.usermanagementsystem.service.JwtService;
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

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
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

        LoginResponse response = new LoginResponse(
                token,
                user.getUserName(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}