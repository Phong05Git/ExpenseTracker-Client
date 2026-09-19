package com.example.expensetracker.domain.usecase.auth;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.domain.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public final class LoginUseCase {
    private final AuthRepository authRepository;

    @Inject
    public LoginUseCase(
            AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Single<AuthResponseDto> execute(
            String username,
            String password) {
        return authRepository
                .login(username, password)
                .flatMap(response ->
                        authRepository
                                .saveTokenData(response)
                                .andThen(Single.just(response)));
    }
}
