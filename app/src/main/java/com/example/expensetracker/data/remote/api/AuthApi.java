package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.data.remote.dto.auth.LoginRequestDto;
import com.example.expensetracker.data.remote.dto.auth.RefreshTokenRequestDto;
import com.example.expensetracker.data.remote.dto.auth.RegisterRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/auth/register")
    Call<AuthResponseDto> register(
            @Body RegisterRequestDto request);

    @POST("api/auth/login")
    Call<AuthResponseDto> login(
            @Body LoginRequestDto request);

    @POST("api/auth/refresh")
    Call<AuthResponseDto> refreshToken(
            @Body RefreshTokenRequestDto request);

    @POST("api/auth/logout")
    Call<Void> logout(
            @Body RefreshTokenRequestDto request);
}