package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.User;
import com.example.expensetracker.domain.usecase.user.GetProfileUseCase;
import com.example.expensetracker.domain.usecase.user.UpdateProfileUseCase;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

@HiltViewModel
public final class ProfileViewModel extends ViewModel {
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private final MutableLiveData<Resource<User>> profileState =
            new MutableLiveData<>(Resource.idle());

    private final MutableLiveData<Resource<User>> updateState =
            new MutableLiveData<>(Resource.idle());

    @Inject
    public ProfileViewModel(
            GetProfileUseCase getProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase) {
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
    }

    public LiveData<Resource<User>> getProfileState() {
        return profileState;
    }

    public LiveData<Resource<User>> getUpdateState() {
        return updateState;
    }

    public void loadProfile() {
        profileState.setValue(Resource.loading());

        disposables.add(
                getProfileUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user ->
                                        profileState.setValue(
                                                Resource.success(user)),
                                throwable ->
                                        profileState.setValue(
                                                Resource.error(
                                                        getErrorMessage(
                                                                throwable)))));
    }

    public void updateProfile(
            String fullName,
            String email) {

        updateState.setValue(Resource.loading());

        disposables.add(
                updateProfileUseCase.execute(
                                fullName,
                                email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    updateState.setValue(
                                            Resource.success(user));

                                    profileState.setValue(
                                            Resource.success(user));
                                },
                                throwable ->
                                        updateState.setValue(
                                                Resource.error(
                                                        getErrorMessage(
                                                                throwable)))));
    }

    private String getErrorMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            if (httpException.code() == 400) {
                return "Thông tin hồ sơ không hợp lệ.";
            }
            if (httpException.code() == 401) {
                return "Phiên đăng nhập đã hết hạn.";
            }
            if (httpException.code() == 409) {
                return "Email đã được sử dụng.";
            }
        }
        return com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Không thể cập nhật hồ sơ. Vui lòng thử lại.");
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}