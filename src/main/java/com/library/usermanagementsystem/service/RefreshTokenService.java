package com.library.usermanagementsystem.service;

import com.library.usermanagementsystem.entity.RefreshToken;
import com.library.usermanagementsystem.exception.RefreshTokenException;
import com.library.usermanagementsystem.repository.RefreshTokenRepository;
import com.library.usermanagementsystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String userName) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserName(userName);

        refreshToken.setExpiryDate(
                new Timestamp(
                        System.currentTimeMillis()
                                + 1000L * 60 * 60 * 24 * 7
                )
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RefreshTokenException("Refresh token not found")
                );

        if (refreshToken.getExpiryDate().before(
                new Timestamp(System.currentTimeMillis())
        )) {
            refreshTokenRepository.delete(refreshToken);

            throw new RefreshTokenException("Refresh token expired");
        }

        return refreshToken;
    }

    public String refreshAccessToken(String token) {

        RefreshToken refreshToken = verifyRefreshToken(token);

        return jwtService.generateToken(
                refreshToken.getUserName(),
                getUserRole(refreshToken.getUserName())
        );
    }

    private String getUserRole(String userName) {

        return userRepository
                .findByUserName(userName)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                )
                .getRole();
    }

    public void deleteRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RefreshTokenException("Refresh token not found")
                );

        refreshTokenRepository.delete(refreshToken);
    }

}