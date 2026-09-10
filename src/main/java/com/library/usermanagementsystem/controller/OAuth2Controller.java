package com.library.usermanagementsystem.controller;

import com.library.usermanagementsystem.entity.RefreshToken;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.repository.UserRepository;
import com.library.usermanagementsystem.service.JwtService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.library.usermanagementsystem.dto.LoginResponse;
import com.library.usermanagementsystem.service.RefreshTokenService;

@RestController
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public OAuth2Controller(
            UserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/oauth2/success")
    public LoginResponse oauth2Success(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseGet(() -> {

                    User newUser = new User();

                    newUser.setFullName(name);
                    newUser.setUserName(email);
                    newUser.setEmail(email);

                    newUser.setPassword("GOOGLE_LOGIN");

                    newUser.setRole("USER");
                    newUser.setVerified(true);

                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(
                user.getUserName(),
                user.getRole()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getUserName()
                );

        return new LoginResponse(
                token,
                refreshToken.getToken(),
                user.getUserName(),
                user.getRole()
        );
    }
}