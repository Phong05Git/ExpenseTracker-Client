package com.example.expensetracker.data.remote.dto.auth;

public class RefreshTokenRequestDto {
    private final String refreshToken;

    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}