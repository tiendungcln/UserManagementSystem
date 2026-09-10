package com.library.usermanagementsystem.dto;

public class LoginResponse {

    private String token;
    private String userName;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(String token, String userName, String role) {
        this.token = token;
        this.userName = userName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }
}