package com.example.expensetracker.domain.repository;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.domain.model.TokenData;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface AuthRepository {
    Single<AuthResponseDto> login(
            String username,
            String password);

    Single<AuthResponseDto> register(
            String username,
            String password,
            String fullName,
            String email);

    Single<AuthResponseDto> refreshToken(
            String refreshToken);

    Completable logout(
            String refreshToken);

    Completable saveTokenData(
            AuthResponseDto response);

    Single<TokenData> getTokenData();

    Completable clearTokenData();
}