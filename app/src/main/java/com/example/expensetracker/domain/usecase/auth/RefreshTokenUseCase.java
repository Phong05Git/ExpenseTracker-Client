package com.example.expensetracker.domain.usecase.auth;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.domain.model.TokenData;
import com.example.expensetracker.domain.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public final class RefreshTokenUseCase {
    private final AuthRepository authRepository;

    @Inject
    public RefreshTokenUseCase(
            AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Single<AuthResponseDto> execute() {
        return authRepository
                .getTokenData()
                .map(TokenData::getRefreshToken)
                .flatMap(authRepository::refreshToken)
                .flatMap(response ->
                        authRepository
                                .saveTokenData(response)
                                .andThen(Single.just(response)));
    }
}