package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.data.remote.dto.auth.RefreshTokenRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RefreshAuthApi {
    @POST("api/auth/refresh")
    Call<AuthResponseDto> refreshToken(
            @Body RefreshTokenRequestDto request);
}