package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.remote.api.UserApi;
import com.example.expensetracker.data.remote.dto.auth.ChangePasswordRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.users.UpdateProfileDto;
import com.example.expensetracker.data.remote.dto.users.UserProfileDto;
import com.example.expensetracker.data.remote.mapper.UserMapper;
import com.example.expensetracker.domain.model.User;
import com.example.expensetracker.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

@Singleton
public final class UserRepositoryImpl
        implements UserRepository {
    private final UserApi userApi;

    @Inject
    public UserRepositoryImpl(
            UserApi userApi) {
        this.userApi = userApi;
    }

    @Override
    public Single<User> getProfile() {
        return executeUser(
                userApi.getProfile());
    }

    @Override
    public Single<User> updateProfile(
            String fullName,
            String email) {
        return executeUser(
                userApi.updateProfile(
                        new UpdateProfileDto(
                                fullName,
                                email)));
    }

    @Override
    public Completable changePassword(
            String currentPassword,
            String newPassword) {
        return executeVoid(
                userApi.changePassword(
                        new ChangePasswordRequestDto(
                                currentPassword,
                                newPassword)));
    }

    private Single<User> executeUser(
            Call<ApiResponseDto<UserProfileDto>> call) {
        return Single.fromCallable(() -> {
                    Response<ApiResponseDto<UserProfileDto>> response =
                            call.execute();

                    if (!response.isSuccessful()) {
                        throw new HttpException(response);
                    }

                    ApiResponseDto<UserProfileDto> body =
                            response.body();

                    if (body == null ||
                            !body.isSuccess() ||
                            body.getData() == null) {
                        throw new IllegalStateException(
                                body != null &&
                                        body.getMessage() != null
                                        ? body.getMessage()
                                        : "User response is invalid.");
                    }

                    return UserMapper.toDomain(
                            body.getData());
                })
                .subscribeOn(Schedulers.io());
    }

    private Completable executeVoid(
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