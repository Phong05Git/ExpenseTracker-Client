package com.example.expensetracker.domain.usecase.user;

import com.example.expensetracker.domain.model.User;
import com.example.expensetracker.domain.repository.UserRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public final class UpdateProfileUseCase {
    private final UserRepository userRepository;

    @Inject
    public UpdateProfileUseCase(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Single<User> execute(
            String fullName,
            String email) {
        return userRepository.updateProfile(
                fullName,
                email);
    }
}