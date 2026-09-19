package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.data.remote.api.AuthApi;
import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.data.remote.dto.auth.LoginRequestDto;
import com.example.expensetracker.data.remote.dto.auth.RefreshTokenRequestDto;
import com.example.expensetracker.data.remote.dto.auth.RegisterRequestDto;
import com.example.expensetracker.domain.model.TokenData;
import com.example.expensetracker.domain.repository.AuthRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Call;
import retrofit2.Response;

@Singleton
public final class AuthRepositoryImpl implements AuthRepository {
    private final AuthApi authApi;
    private final TokenManager tokenManager;

    @Inject
    public AuthRepositoryImpl(
            AuthApi authApi,
            TokenManager tokenManager) {
        this.authApi = authApi;
        this.tokenManager = tokenManager;
    }

    @Override
    public Single<AuthResponseDto> login(
            String username,
            String password) {
        return executeAuth(
                authApi.login(
                        new LoginRequestDto(
                                username,
                                password)));
    }

    @Override
    public Single<AuthResponseDto> register(
            String username,
            String password,
            String fullName,
            String email) {
        return executeAuth(
                authApi.register(
                        new RegisterRequestDto(
                                username,
                                password,
                                fullName,
                                email)));
    }

    @Override
    public Single<AuthResponseDto> refreshToken(
            String refreshToken) {
        return executeAuth(
                authApi.refreshToken(
                        new RefreshTokenRequestDto(
                                refreshToken)));
    }

    @Override
    public Completable logout(
            String refreshToken) {
        return executeLogout(
                authApi.logout(
                        new RefreshTokenRequestDto(
                                refreshToken)));
    }

    @Override
    public Completable saveTokenData(
            AuthResponseDto response) {
        TokenData tokenData =
                new TokenData(
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        response.getAccessTokenExpiresAt(),
                        response.getRefreshTokenExpiresAt());

        return tokenManager.saveTokenData(tokenData);
    }

    @Override
    public Single<TokenData> getTokenData() {
        return tokenManager.getTokenData();
    }

    @Override
    public Completable clearTokenData() {
        return tokenManager.clear();
    }

    private Single<AuthResponseDto> executeAuth(
            Call<AuthResponseDto> call) {
        return Single.fromCallable(() -> {
                    Response<AuthResponseDto> response =
                            call.execute();

                    if (!response.isSuccessful()) {
                        throw new HttpException(response);
                    }

                    AuthResponseDto body =
                            response.body();

                    if (body == null) {
                        throw new IllegalStateException(
                                "Authentication response is empty.");
                    }

                    return body;
                })
                .subscribeOn(Schedulers.io());
    }

    private Completable executeLogout(
            Call<Void> call) {
        return Completable.fromAction(() -> {
                    Response<Void> response =
                            call.execute();

                    if (!response.isSuccessful()) {
                        throw new HttpException(response);
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}