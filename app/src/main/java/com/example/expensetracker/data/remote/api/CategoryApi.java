package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.categories.CategoryDto;
import com.example.expensetracker.data.remote.dto.categories.CreateCategoryRequestDto;
import com.example.expensetracker.data.remote.dto.categories.UpdateCategoryRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryApi {

    @GET("api/categories")
    Call<ApiResponseDto<List<CategoryDto>>> getCategories();

    @POST("api/categories")
    Call<ApiResponseDto<CategoryDto>> createCategory(@Body CreateCategoryRequestDto request);

    @PUT("api/categories/{id}")
    Call<ApiResponseDto<CategoryDto>> updateCategory(@Path("id") int id, @Body UpdateCategoryRequestDto request);

    @DELETE("api/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);
}