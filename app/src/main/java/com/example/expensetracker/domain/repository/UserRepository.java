package com.example.expensetracker.domain.repository;

import com.example.expensetracker.domain.model.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
    Single<User> getProfile();

    Single<User> updateProfile(
            String fullName,
            String email);

    Completable changePassword(
            String currentPassword,
            String newPassword);
}