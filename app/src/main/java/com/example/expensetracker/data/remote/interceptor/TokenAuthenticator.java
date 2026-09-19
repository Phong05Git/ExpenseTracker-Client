package com.example.expensetracker.data.remote.interceptor;

import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.data.remote.api.RefreshAuthApi;
import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.data.remote.dto.auth.RefreshTokenRequestDto;
import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.di.RefreshAuthApiQualifier;
import com.example.expensetracker.domain.model.TokenData;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import retrofit2.Call;

@Singleton
public final class TokenAuthenticator implements Authenticator {
    private final TokenManager tokenManager;
    private final RefreshAuthApi refreshAuthApi;
    private final SessionManager sessionManager;

    @Inject
    public TokenAuthenticator(
            TokenManager tokenManager,
            @RefreshAuthApiQualifier RefreshAuthApi refreshAuthApi,
            SessionManager sessionManager) {
        this.tokenManager = tokenManager;
        this.refreshAuthApi = refreshAuthApi;
        this.sessionManager = sessionManager;
    }

    @Override
    public synchronized Request authenticate(
            Route route,
            Response response) throws IOException {

        if (responseCount(response) >= 2) {
            return null;
        }

        long refreshGeneration =
                sessionManager.getSessionGeneration();

        String failedAuthorization =
                response.request().header("Authorization");

        TokenData currentTokenData;

        try {
            currentTokenData =
                    tokenManager.getTokenData().blockingGet();
        } catch (Exception e) {
            expireSession();
            return null;
        }

        String currentAccessToken =
                currentTokenData.getAccessToken();

        if (currentAccessToken == null
                || currentAccessToken.isBlank()) {
            expireSession();
            return null;
        }

        String currentAuthorization =
                "Bearer " + currentAccessToken;

        if (failedAuthorization != null
                && !failedAuthorization.equals(currentAuthorization)) {

            return response.request()
                    .newBuilder()
                    .header(
                            "Authorization",
                            currentAuthorization)
                    .build();
        }

        String refreshToken =
                currentTokenData.getRefreshToken();

        if (refreshToken == null
                || refreshToken.isBlank()) {
            expireSession();
            return null;
        }

        Call<AuthResponseDto> refreshCall =
                refreshAuthApi.refreshToken(
                        new RefreshTokenRequestDto(refreshToken));

        retrofit2.Response<AuthResponseDto> refreshResponse;

        try {
            refreshResponse =
                    refreshCall.execute();
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        }

        if (!refreshResponse.isSuccessful()) {
            expireSession();
            return null;
        }

        AuthResponseDto authResponse =
                refreshResponse.body();

        if (authResponse == null
                || authResponse.getAccessToken() == null
                || authResponse.getAccessToken().isBlank()
                || authResponse.getRefreshToken() == null
                || authResponse.getRefreshToken().isBlank()
                || authResponse.getAccessTokenExpiresAt() == null
                || authResponse.getRefreshTokenExpiresAt() == null) {
            expireSession();
            return null;
        }

        if (sessionManager.getSessionGeneration()
                != refreshGeneration) {
            return null;
        }

        TokenData newTokenData =
                new TokenData(
                        authResponse.getAccessToken(),
                        authResponse.getRefreshToken(),
                        authResponse.getAccessTokenExpiresAt(),
                        authResponse.getRefreshTokenExpiresAt());

        try {
            tokenManager.saveTokenData(
                    newTokenData).blockingAwait();
        } catch (Exception e) {
            expireSession();
            return null;
        }

        if (sessionManager.getSessionGeneration()
                != refreshGeneration) {
            return null;
        }

        return response.request()
                .newBuilder()
                .header(
                        "Authorization",
                        "Bearer " +
                                newTokenData.getAccessToken())
                .build();
    }

    private void expireSession() {
        try {
            tokenManager.clear().blockingAwait();
        } catch (Exception ignored) {
        }

        sessionManager.notifySessionExpired();
    }

    private int responseCount(Response response) {
        int count = 1;

        while ((response = response.priorResponse()) != null) {
            count++;
        }

        return count;
    }
}