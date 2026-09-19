package com.example.expensetracker.domain.repository;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.StatisticsData;

import io.reactivex.rxjava3.core.Single;

public interface ReportRepository {
    Single<Resource<StatisticsData>> getStatistics(
            int period,
            String referenceDate,
            String startDate,
            String endDate);
}