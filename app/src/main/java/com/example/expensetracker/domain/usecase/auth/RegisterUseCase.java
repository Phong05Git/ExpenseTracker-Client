package com.example.expensetracker.domain.usecase.auth;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.domain.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public final class RegisterUseCase {
    private final AuthRepository authRepository;

    @Inject
    public RegisterUseCase(
            AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Single<AuthResponseDto> execute(
            String username,
            String password,
            String fullName,
            String email) {
        return authRepository.register(
                username,
                password,
                fullName,
                email);
    }
}