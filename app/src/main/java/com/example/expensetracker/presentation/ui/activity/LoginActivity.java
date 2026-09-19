package com.example.expensetracker.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.presentation.viewmodel.LoginViewModel;
import com.example.expensetracker.util.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;

    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private MaterialButton buttonLogin;
    private TextView textRegister;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_login);

        initializeViews();

        loginViewModel =
                new ViewModelProvider(this)
                        .get(LoginViewModel.class);

        setupListeners();
        observeLogin();
        handleRegisterSuccess();
    }

    private void initializeViews() {
        editUsername = findViewById(R.id.editUsername);

        editPassword = findViewById(R.id.editPassword);

        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        buttonLogin = findViewById(R.id.buttonLogin);

        textRegister = findViewById(R.id.textRegister);
    }

    private void setupListeners() {
        buttonLogin.setOnClickListener(
                view -> performLogin());

        ValidationUtils.bindLiveValidation(editUsername, usernameLayout,
                () -> ValidationUtils.isBlank(getText(editUsername)) ? "Vui lòng nhập tên đăng nhập." : null);
        ValidationUtils.bindLiveValidation(editPassword, passwordLayout,
                () -> ValidationUtils.isBlank(getText(editPassword)) ? "Vui lòng nhập mật khẩu." : null);

        setupRegisterText();
    }

    private void setupRegisterText() {
        String text =
                "Chưa có tài khoản? Đăng ký";

        SpannableString spannableString =
                new SpannableString(text);

        int start =
                text.indexOf("Đăng ký");

        spannableString.setSpan(
                new ForegroundColorSpan(
                        getColor(
                                R.color.primary_green)),
                start,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textRegister.setText(spannableString);

        textRegister.setOnClickListener(
                view -> openRegister());
    }

    private void performLogin() {
        String username = getText(editUsername);

        String password = getText(editPassword);

        boolean valid = ValidationUtils.setError(usernameLayout,
                ValidationUtils.isBlank(username) ? "Vui lòng nhập tên đăng nhập." : null);
        valid &= ValidationUtils.setError(passwordLayout,
                ValidationUtils.isBlank(password) ? "Vui lòng nhập mật khẩu." : null);

        if (!valid) return;

        loginViewModel.login(
                username,
                password);
    }

    private String getText(
            TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString();
    }

    private void observeLogin() {
        loginViewModel
                .getLoginState()
                .observe(
                        this,
                        resource -> {
                            if (resource == null) {
                                return;
                            }

                            switch (resource.getStatus()) {
                                case LOADING:
                                    setLoginEnabled(false);
                                    break;

                                case SUCCESS:
                                    setLoginEnabled(true);
                                    openAuthenticatedApp();
                                    break;

                                case ERROR:
                                    setLoginEnabled(true);
                                    showLoginError(resource.getMessage());
                                    break;

                                case IDLE:
                                default:
                                    setLoginEnabled(true);
                                    break;
                            }
                        });
    }

    private void showLoginError(String message) {
        if (message == null || message.isBlank()) {
            message = "Đăng nhập thất bại.";
        }

        android.widget.Toast.makeText(
                        this,
                        message,
                        android.widget.Toast.LENGTH_LONG)
                .show();
    }

    private void handleRegisterSuccess() {
        Intent intent = getIntent();

        boolean registerSuccess =
                intent.getBooleanExtra(
                        "register_success",
                        false);

        if (!registerSuccess) {
            return;
        }

        String registeredUsername =
                intent.getStringExtra("registered_username");

        if (registeredUsername != null && !registeredUsername.isBlank()) {
            editUsername.setText(registeredUsername);

            editPassword.requestFocus();
        }

        android.widget.Toast.makeText(
                        this,
                        "Đăng ký thành công. Hãy đăng nhập.",
                        android.widget.Toast.LENGTH_LONG)
                .show();
    }

    private void setLoginEnabled(
            boolean enabled) {
        buttonLogin.setEnabled(enabled);
        editUsername.setEnabled(enabled);
        editPassword.setEnabled(enabled);
    }

    private void openRegister() {
        Intent intent =
                new Intent(
                        this,
                        RegisterActivity.class);

        startActivity(intent);
    }

    private void openAuthenticatedApp() {
        Intent intent =
                new Intent(
                        this,
                        SplashActivity.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
