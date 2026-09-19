package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.common.PagedResultDto;
import com.example.expensetracker.data.remote.dto.transactions.CreateTransactionRequestDto;
import com.example.expensetracker.data.remote.dto.transactions.TransactionDto;
import com.example.expensetracker.data.remote.dto.transactions.UpdateTransactionRequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TransactionApi {

    @GET("api/transactions")
    Call<ApiResponseDto<PagedResultDto<TransactionDto>>> getTransactions(
            @Query("fromDate") String fromDate,
            @Query("toDate") String toDate,
            @Query("categoryId") Integer categoryId,
            @Query("type") Integer type,
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("pageSize") int pageSize);

    @GET("api/transactions/by-date")
    Call<ApiResponseDto<List<TransactionDto>>> getTransactionsByDate(
            @Query("date") String date);

    @POST("api/transactions")
    Call<ApiResponseDto<TransactionDto>> createTransaction(
            @Body CreateTransactionRequestDto request);

    @PUT("api/transactions/{id}")
    Call<ApiResponseDto<TransactionDto>> updateTransaction(
            @Path("id") int id,
            @Body UpdateTransactionRequestDto request);

    @DELETE("api/transactions/{id}")
    Call<Void> deleteTransaction(
            @Path("id") int id);
}