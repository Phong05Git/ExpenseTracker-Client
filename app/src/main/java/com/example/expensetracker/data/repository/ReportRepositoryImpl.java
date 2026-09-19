package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.remote.api.ReportApi;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.reports.CategoryBreakdownDto;
import com.example.expensetracker.data.remote.dto.reports.StatisticsDto;
import com.example.expensetracker.data.remote.dto.reports.StatisticsPointDto;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.CategoryBreakdown;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.domain.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class ReportRepositoryImpl implements ReportRepository {

    private final ReportApi reportApi;

    @Inject
    public ReportRepositoryImpl(ReportApi reportApi) {
        this.reportApi = reportApi;
    }

    @Override
    public Single<Resource<StatisticsData>> getStatistics(
            int period,
            String referenceDate,
            String startDate,
            String endDate) {

        return Single.create(emitter -> {
            try {
                Response<ApiResponseDto<StatisticsDto>> response =
                        reportApi.getStatistics(
                                        period,
                                        referenceDate,
                                        startDate,
                                        endDate)
                                .execute();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    StatisticsDto dto =
                            response.body().getData();

                    emitter.onSuccess(
                            Resource.success(
                                    mapStatistics(dto)));

                    return;
                }

                emitter.onSuccess(
                        Resource.error(
                                "Không thể tải dữ liệu thống kê."));
            } catch (Exception e) {
                emitter.onSuccess(
                        Resource.error(
                                "Lỗi kết nối máy chủ."));
            }
        });
    }

    private StatisticsData mapStatistics(
            StatisticsDto dto) {

        List<StatisticsData.StatisticsPoint> timeline =
                new ArrayList<>();

        if (dto.getTimeline() != null) {
            for (StatisticsPointDto item :
                    dto.getTimeline()) {

                timeline.add(
                        new StatisticsData.StatisticsPoint(
                                item.getDate(),
                                item.getIncome(),
                                item.getExpense()));
            }
        }

        List<CategoryBreakdown> categoryBreakdown =
                new ArrayList<>();

        if (dto.getExpenseCategoryBreakdown() != null) {
            for (CategoryBreakdownDto item :
                    dto.getExpenseCategoryBreakdown()) {

                categoryBreakdown.add(
                        new CategoryBreakdown(
                                item.getCategoryId(),
                                item.getCategoryName(),
                                item.getCategoryColor(),
                                item.getAmount(),
                                item.getPercentage()));
            }
        }

        List<CategoryBreakdown> incomeCategoryBreakdown =
                new ArrayList<>();

        if (dto.getIncomeCategoryBreakdown() != null) {
            for (CategoryBreakdownDto item :
                    dto.getIncomeCategoryBreakdown()) {

                incomeCategoryBreakdown.add(
                        new CategoryBreakdown(
                                item.getCategoryId(),
                                item.getCategoryName(),
                                item.getCategoryColor(),
                                item.getAmount(),
                                item.getPercentage()));
            }
        }

        return new StatisticsData(
                dto.getPeriod(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getTotalIncome(),
                dto.getTotalExpense(),
                timeline,
                categoryBreakdown,
                incomeCategoryBreakdown);
    }
}