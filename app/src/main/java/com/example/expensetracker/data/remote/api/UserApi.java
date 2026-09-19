package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.auth.ChangePasswordRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.users.UpdateProfileDto;
import com.example.expensetracker.data.remote.dto.users.UserProfileDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApi {
    @GET("api/users/profile")
    Call<ApiResponseDto<UserProfileDto>> getProfile();

    @PUT("api/users/profile")
    Call<ApiResponseDto<UserProfileDto>> updateProfile(
            @Body UpdateProfileDto request);

    @POST("api/users/change-password")
    Call<Void> changePassword(
            @Body ChangePasswordRequestDto request);
}