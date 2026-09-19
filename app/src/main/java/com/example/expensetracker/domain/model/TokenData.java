package com.example.expensetracker.domain.model;

public final class TokenData {
    private final String accessToken;
    private final String refreshToken;
    private final String accessTokenExpiresAt;
    private final String refreshTokenExpiresAt;

    public TokenData(
            String accessToken,
            String refreshToken,
            String accessTokenExpiresAt,
            String refreshTokenExpiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

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