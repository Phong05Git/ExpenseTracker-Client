package com.example.expensetracker.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.domain.usecase.user.ChangePasswordUseCase;
import com.example.expensetracker.util.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

@AndroidEntryPoint
public final class ChangePasswordActivity
        extends AppCompatActivity {
    @Inject
    ChangePasswordUseCase changePasswordUseCase;

    @Inject
    TokenManager tokenManager;

    @Inject
    SessionManager sessionManager;

    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private TextInputEditText editCurrentPassword;
    private TextInputEditText editNewPassword;
    private TextInputEditText editConfirmPassword;
    private MaterialButton buttonSave;
    private TextInputLayout currentPasswordLayout;
    private TextInputLayout newPasswordLayout;
    private TextInputLayout confirmPasswordLayout;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_change_password);

        initializeViews();
        setupToolbar();

        buttonSave.setOnClickListener(
                view -> changePassword());

        ValidationUtils.bindLiveValidation(editCurrentPassword, currentPasswordLayout,
                () -> ValidationUtils.isBlank(getText(editCurrentPassword)) ? "Vui lòng nhập mật khẩu hiện tại." : null);
        ValidationUtils.bindLiveValidation(editNewPassword, newPasswordLayout,
                () -> ValidationUtils.passwordError(getText(editNewPassword)));
        ValidationUtils.bindLiveValidation(editConfirmPassword, confirmPasswordLayout,
                () -> ValidationUtils.confirmPasswordError(getText(editNewPassword), getText(editConfirmPassword)));
    }

    private void initializeViews() {
        editCurrentPassword = findViewById(R.id.editCurrentPassword);

        editNewPassword = findViewById(R.id.editNewPassword);

        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        buttonSave = findViewById(R.id.buttonSave);
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(
                view -> finish());
    }

    private void changePassword() {
        String currentPassword = getText(editCurrentPassword);

        String newPassword = getText(editNewPassword);

        String confirmPassword = getText(editConfirmPassword);

        boolean valid = ValidationUtils.setError(currentPasswordLayout,
                ValidationUtils.isBlank(currentPassword) ? "Vui lòng nhập mật khẩu hiện tại." : null);
        valid &= ValidationUtils.setError(newPasswordLayout, ValidationUtils.passwordError(newPassword));
        valid &= ValidationUtils.setError(confirmPasswordLayout,
                ValidationUtils.confirmPasswordError(newPassword, confirmPassword));
        if (!valid) return;

        buttonSave.setEnabled(false);

        disposables.add(
                changePasswordUseCase
                        .execute(
                                currentPassword,
                                newPassword)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onChangePasswordSuccess,
                                this::onChangePasswordError));
    }

    private void onChangePasswordSuccess() {
        disposables.add(
                tokenManager
                        .clear()
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    sessionManager.reset();
                                    openLoginScreen();
                                },
                                throwable ->
                                        openLoginScreen()));
    }

    private void openLoginScreen() {
        Toast.makeText(
                        this,
                        "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.",
                        Toast.LENGTH_LONG)
                .show();

        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void onChangePasswordError(
            Throwable throwable) {
        buttonSave.setEnabled(true);

        String message = getErrorMessage(throwable);

        Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG)
                .show();
    }

    private String getErrorMessage(
            Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException =
                    (HttpException) throwable;

            if (httpException.code() == 400) {
                return "Mật khẩu hiện tại không chính xác hoặc thông tin không hợp lệ.";
            }

            if (httpException.code() == 401) {
                return "Phiên đăng nhập đã hết hạn.";
            }

            return "Không thể đổi mật khẩu. Vui lòng thử lại.";
        }

        if (throwable instanceof IOException) {
            return "Không thể kết nối đến máy chủ.";
        }

        return "Đã xảy ra lỗi. Vui lòng thử lại.";
    }

    private String getText(
            TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText()
                .toString();
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
