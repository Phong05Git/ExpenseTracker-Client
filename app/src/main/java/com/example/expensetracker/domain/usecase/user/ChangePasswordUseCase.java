package com.example.expensetracker.domain.usecase.user;

import com.example.expensetracker.domain.repository.UserRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public final class ChangePasswordUseCase {
    private final UserRepository userRepository;

    @Inject
    public ChangePasswordUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Completable execute(String currentPassword, String newPassword) {
        return userRepository.changePassword(
                currentPassword,
                newPassword);
    }
}