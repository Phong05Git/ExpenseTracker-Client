package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.budgets.BudgetDto;
import com.example.expensetracker.data.remote.dto.budgets.CreateBudgetRequestDto;
import com.example.expensetracker.data.remote.dto.budgets.UpdateBudgetRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BudgetApi {

    @GET("api/budgets")
    Call<ApiResponseDto<List<BudgetDto>>> getBudgets();

    @POST("api/budgets")
    Call<ApiResponseDto<BudgetDto>> createBudget(@Body CreateBudgetRequestDto request);

    @PUT("api/budgets/{id}")
    Call<ApiResponseDto<BudgetDto>> updateBudget(
            @Path("id") int id,
            @Body UpdateBudgetRequestDto request);

    @DELETE("api/budgets/{id}")
    Call<Void> deleteBudget(@Path("id") int id);
}