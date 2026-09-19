package com.example.expensetracker.data.remote.dto.auth;

public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private String accessTokenExpiresAt;
    private String refreshTokenExpiresAt;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public String getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }
}