package com.example.expensetracker.domain.usecase.statistics;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.domain.repository.ReportRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetStatisticsUseCase {

    private final ReportRepository reportRepository;

    @Inject
    public GetStatisticsUseCase(
            ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Single<Resource<StatisticsData>> execute(
            int period,
            String referenceDate,
            String startDate,
            String endDate) {

        return reportRepository.getStatistics(
                period,
                referenceDate,
                startDate,
                endDate);
    }
}