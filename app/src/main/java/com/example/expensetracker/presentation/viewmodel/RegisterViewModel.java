package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.data.remote.dto.auth.AuthResponseDto;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.usecase.auth.RegisterUseCase;
import com.example.expensetracker.util.ValidationUtils;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.HttpException;

@HiltViewModel
public final class RegisterViewModel extends ViewModel {
    private final RegisterUseCase registerUseCase;
    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private final MutableLiveData<Resource<AuthResponseDto>> registerState =
            new MutableLiveData<>(Resource.idle());

    @Inject
    public RegisterViewModel(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    public LiveData<Resource<AuthResponseDto>> getRegisterState() {
        return registerState;
    }

    public void register(
            String fullName,
            String username,
            String password,
            String confirmPassword,
            String email) {

        String validationMessage =
                validate(
                        fullName,
                        username,
                        password,
                        confirmPassword,
                        email);

        if (validationMessage != null) {
            registerState.setValue(
                    Resource.error(validationMessage));
            return;
        }

        registerState.setValue(Resource.loading());

        disposables.add(
                registerUseCase.execute(
                                username.trim(),
                                password,
                                fullName.trim(),
                                email.trim())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response ->
                                        registerState.setValue(
                                                Resource.success(response)),
                                throwable ->
                                        registerState.setValue(
                                                Resource.error(
                                                        getErrorMessage(
                                                                throwable)))));
    }

    private String validate(
            String fullName,
            String username,
            String password,
            String confirmPassword,
            String email) {

        String error = ValidationUtils.fullNameError(fullName);
        if (error != null) return error;
        error = ValidationUtils.usernameError(username);
        if (error != null) return error;
        error = ValidationUtils.emailError(email);
        if (error != null) return error;
        error = ValidationUtils.passwordError(password);
        if (error != null) return error;
        return ValidationUtils.confirmPasswordError(password, confirmPassword);
    }

    private String getErrorMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            if (httpException.code() == 400) {
                return "Thông tin đăng ký không hợp lệ.";
            }
            if (httpException.code() == 409) {
                return "Tên đăng nhập hoặc email đã được sử dụng.";
            }
        }
        return com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Không thể đăng ký. Vui lòng thử lại.");
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
