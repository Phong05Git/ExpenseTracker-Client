package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.domain.usecase.statistics.GetStatisticsUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final GetStatisticsUseCase getStatisticsUseCase;
    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private Disposable statisticsRequest;

    private final MutableLiveData<Resource<StatisticsData>> statistics =
            new MutableLiveData<>();

    @Inject
    public StatisticsViewModel(
            GetStatisticsUseCase getStatisticsUseCase) {

        this.getStatisticsUseCase =
                getStatisticsUseCase;
    }

    public LiveData<Resource<StatisticsData>> getStatistics() {
        return statistics;
    }

    public void loadStatistics(
            int period,
            String referenceDate,
            String startDate,
            String endDate) {

        statistics.setValue(
                Resource.loading());

        if (statisticsRequest != null) {
            statisticsRequest.dispose();
            disposables.remove(statisticsRequest);
        }

        statisticsRequest =
                getStatisticsUseCase.execute(
                                period,
                                referenceDate,
                                startDate,
                                endDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                statistics::setValue,
                                throwable ->
                                        statistics.setValue(
                                                Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi tải dữ liệu thống kê."))));

        disposables.add(
                statisticsRequest);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
