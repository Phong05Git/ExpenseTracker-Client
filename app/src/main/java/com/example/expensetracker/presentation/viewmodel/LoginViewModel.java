package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.usecase.auth.LoginUseCase;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.HttpException;

@HiltViewModel
public final class LoginViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private final MutableLiveData<Resource<AuthResponseDto>> loginState =
            new MutableLiveData<>(Resource.idle());

    @Inject
    public LoginViewModel(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public LiveData<Resource<AuthResponseDto>> getLoginState() {
        return loginState;
    }

    public void login(String username, String password) {
        if (username == null ||
                username.isBlank() ||
                password == null ||
                password.isBlank()) {
            loginState.setValue(
                    Resource.error(
                            "Username and password are required."));
            return;
        }

        loginState.setValue(Resource.loading());

        disposables.add(
                loginUseCase.execute(
                                username.trim(),
                                password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response ->
                                        loginState.setValue(
                                                Resource.success(response)),
                                throwable ->
                                        loginState.setValue(
                                                Resource.error(
                                                        getErrorMessage(
                                                                throwable)))));
    }

    private String getErrorMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException =
                    (HttpException) throwable;

            if (httpException.code() == 401) {
                return "Tên đăng nhập hoặc mật khẩu không chính xác.";
            }

            if (httpException.code() == 400) {
                return "Thông tin đăng nhập không hợp lệ.";
            }

            return "Không thể đăng nhập. Vui lòng thử lại.";
        }

        if (throwable instanceof IOException) {
            return "Không thể kết nối đến máy chủ.";
        }

        return "Đăng nhập thất bại. Vui lòng thử lại.";
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}