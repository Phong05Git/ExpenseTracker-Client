package com.example.expensetracker.domain.usecase.auth;

import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.domain.model.TokenData;
import com.example.expensetracker.domain.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public final class LogoutUseCase {
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    @Inject
    public LogoutUseCase(
            AuthRepository authRepository,
            SessionManager sessionManager) {
        this.authRepository = authRepository;
        this.sessionManager = sessionManager;
    }

    public Completable execute() {
        return authRepository
                .getTokenData()
                .map(TokenData::getRefreshToken)
                .flatMapCompletable(refreshToken -> {
                    sessionManager.invalidateSession();

                    return authRepository
                            .logout(refreshToken)
                            .onErrorComplete()
                            .andThen(authRepository.clearTokenData());
                })
                .onErrorResumeNext(throwable ->
                        authRepository
                                .clearTokenData()
                                .andThen(
                                        Completable.fromAction(
                                                sessionManager::invalidateSession)));
    }
}