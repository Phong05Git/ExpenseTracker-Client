package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.domain.usecase.statistics.GetStatisticsUseCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class OverviewViewModel extends ViewModel {

    private final GetStatisticsUseCase getStatisticsUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Resource<StatisticsData>> statistics = new MutableLiveData<>();
    private static final long CACHE_TTL_MS = 60_000L;
    private final Map<String, CachedStatistics> cache = new HashMap<>();

    private Disposable statisticsRequest;
    private String inFlightKey;

    @Inject
    public OverviewViewModel(GetStatisticsUseCase getStatisticsUseCase) {
        this.getStatisticsUseCase = getStatisticsUseCase;
        statistics.setValue(Resource.idle());
    }

    public LiveData<Resource<StatisticsData>> getStatistics() {
        return statistics;
    }

    public void loadStatistics(
            int period,
            String referenceDate,
            String startDate,
            String endDate) {
        loadStatistics(period, referenceDate, startDate, endDate, false);
    }

    public void refreshStatistics(
            int period,
            String referenceDate,
            String startDate,
            String endDate) {
        loadStatistics(period, referenceDate, startDate, endDate, true);
    }

    private void loadStatistics(
            int period,
            String referenceDate,
            String startDate,
            String endDate,
            boolean forceRefresh) {

        String requestKey = buildRequestKey(period, referenceDate, startDate, endDate);

        if (!forceRefresh) {
            CachedStatistics cached = cache.get(requestKey);
            if (cached != null
                    && System.currentTimeMillis() - cached.cachedAt < CACHE_TTL_MS) {
                if (statisticsRequest != null && !statisticsRequest.isDisposed()) {
                    statisticsRequest.dispose();
                    disposables.remove(statisticsRequest);
                }
                inFlightKey = null;
                statistics.setValue(Resource.success(cached.data));
                return;
            }
            if (cached != null) {
                cache.remove(requestKey);
            }

            if (Objects.equals(inFlightKey, requestKey)
                    && statisticsRequest != null
                    && !statisticsRequest.isDisposed()) {
                return;
            }
        }

        if (statisticsRequest != null) {
            statisticsRequest.dispose();
            disposables.remove(statisticsRequest);
        }

        inFlightKey = requestKey;
        statistics.setValue(Resource.loading());

        statisticsRequest = getStatisticsUseCase.execute(
                        period,
                        referenceDate,
                        startDate,
                        endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resource -> {
                    if (!Objects.equals(inFlightKey, requestKey)) {
                        return;
                    }

                    if (resource != null
                            && resource.getStatus() == Resource.Status.SUCCESS
                            && resource.getData() != null) {
                        cache.put(requestKey, new CachedStatistics(resource.getData()));
                    }

                    inFlightKey = null;
                    statistics.setValue(resource);
                }, throwable -> {
                    if (!Objects.equals(inFlightKey, requestKey)) {
                        return;
                    }

                    inFlightKey = null;
                    statistics.setValue(Resource.error(
                            com.example.expensetracker.util.ErrorUtils.getErrorMessage(
                                    throwable,
                                    "Lỗi tải dữ liệu thống kê.")));
                });

        disposables.add(statisticsRequest);
    }

    private static final class CachedStatistics {
        private final StatisticsData data;
        private final long cachedAt;

        private CachedStatistics(StatisticsData data) {
            this.data = data;
            this.cachedAt = System.currentTimeMillis();
        }
    }

    private String buildRequestKey(
            int period,
            String referenceDate,
            String startDate,
            String endDate) {
        return period + "|"
                + String.valueOf(referenceDate) + "|"
                + String.valueOf(startDate) + "|"
                + String.valueOf(endDate);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        cache.clear();
        super.onCleared();
    }
}
