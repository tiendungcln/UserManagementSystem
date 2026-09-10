package com.library.usermanagementsystem.dto;

public class LoginResponse {

    private String token;
    private String refreshToken;
    private String userName;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(String token, String refreshToken, String userName, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }
}