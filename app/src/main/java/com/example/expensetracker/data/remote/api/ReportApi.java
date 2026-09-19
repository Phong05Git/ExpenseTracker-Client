package com.example.expensetracker.data.remote.api;

import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.reports.StatisticsDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportApi {
    @GET("api/reports/statistics")
    Call<ApiResponseDto<StatisticsDto>> getStatistics(
            @Query("period") int period,
            @Query("referenceDate") String referenceDate,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate);
}