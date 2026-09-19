package com.example.expensetracker.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.presentation.viewmodel.RegisterViewModel;
import com.example.expensetracker.util.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;

    private TextInputLayout fullNameLayout;
    private TextInputLayout usernameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText editFullName;
    private TextInputEditText editUsername;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirmPassword;

    private MaterialButton buttonRegister;
    private TextView textLogin;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_register);

        initializeViews();

        registerViewModel =
                new ViewModelProvider(this)
                        .get(RegisterViewModel.class);

        setupLoginText();
        setupListeners();
        observeRegister();
    }

    private void initializeViews() {
        fullNameLayout =
                findViewById(R.id.fullNameLayout);

        usernameLayout =
                findViewById(R.id.usernameLayout);

        emailLayout =
                findViewById(R.id.emailLayout);

        passwordLayout =
                findViewById(R.id.passwordLayout);

        confirmPasswordLayout =
                findViewById(
                        R.id.confirmPasswordLayout);

        editFullName =
                findViewById(R.id.editFullName);

        editUsername =
                findViewById(R.id.editUsername);

        editEmail =
                findViewById(R.id.editEmail);

        editPassword =
                findViewById(R.id.editPassword);

        editConfirmPassword =
                findViewById(R.id.editConfirmPassword);

        buttonRegister =
                findViewById(R.id.buttonRegister);

        textLogin =
                findViewById(R.id.textLogin);
    }

    private void setupListeners() {
        buttonRegister.setOnClickListener(
                view -> performRegister());

        textLogin.setOnClickListener(
                view -> openLogin());

        ValidationUtils.bindLiveValidation(editFullName, fullNameLayout,
                () -> ValidationUtils.fullNameError(getText(editFullName)));
        ValidationUtils.bindLiveValidation(editUsername, usernameLayout,
                () -> ValidationUtils.usernameError(getText(editUsername)));
        ValidationUtils.bindLiveValidation(editEmail, emailLayout,
                () -> ValidationUtils.emailError(getText(editEmail)));
        ValidationUtils.bindLiveValidation(editPassword, passwordLayout,
                () -> ValidationUtils.passwordError(getText(editPassword)));
        ValidationUtils.bindLiveValidation(editConfirmPassword, confirmPasswordLayout,
                () -> ValidationUtils.confirmPasswordError(getText(editPassword), getText(editConfirmPassword)));
    }

    private void setupLoginText() {
        String text =
                "Đã có tài khoản? Đăng nhập";

        SpannableString spannableString =
                new SpannableString(text);

        int start =
                text.indexOf("Đăng nhập");

        spannableString.setSpan(
                new ForegroundColorSpan(
                        getColor(
                                R.color.primary_green)),
                start,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textLogin.setText(spannableString);
    }

    private void performRegister() {
        boolean valid = ValidationUtils.setError(fullNameLayout, ValidationUtils.fullNameError(getText(editFullName)));
        valid &= ValidationUtils.setError(usernameLayout, ValidationUtils.usernameError(getText(editUsername)));
        valid &= ValidationUtils.setError(emailLayout, ValidationUtils.emailError(getText(editEmail)));
        valid &= ValidationUtils.setError(passwordLayout, ValidationUtils.passwordError(getText(editPassword)));
        valid &= ValidationUtils.setError(confirmPasswordLayout,
                ValidationUtils.confirmPasswordError(getText(editPassword), getText(editConfirmPassword)));

        if (!valid) return;

        registerViewModel.register(
                getText(editFullName),
                getText(editUsername),
                getText(editPassword),
                getText(editConfirmPassword),
                getText(editEmail));
    }

    private String getText(
            TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString();
    }

    private void observeRegister() {
        registerViewModel
                .getRegisterState()
                .observe(
                        this,
                        resource -> {
                            if (resource == null) {
                                return;
                            }

                            switch (resource.getStatus()) {
                                case LOADING:
                                    setRegisterEnabled(false);
                                    break;

                                case SUCCESS:
                                    setRegisterEnabled(true);
                                    openLoginAfterRegister();
                                    break;

                                case ERROR:
                                    setRegisterEnabled(true);
                                    showRegisterError(
                                            resource.getMessage());
                                    break;

                                case IDLE:
                                default:
                                    setRegisterEnabled(true);
                                    break;
                            }
                        });
    }

    private void showRegisterError(
            String message) {
        if (message == null ||
                message.isBlank()) {
            message = "Đăng ký thất bại.";
        }

        if (message.contains("họ và tên")) {
            fullNameLayout.setError(message);
            return;
        }

        if (message.contains("tên đăng nhập")) {
            usernameLayout.setError(message);
            return;
        }

        if (message.contains("email") ||
                message.contains("Email")) {
            emailLayout.setError(message);
            return;
        }

        if (message.contains("mật khẩu xác nhận")) {
            confirmPasswordLayout.setError(message);
            return;
        }

        if (message.contains("Mật khẩu")) {
            passwordLayout.setError(message);
            return;
        }

        Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG)
                .show();
    }

    private void setRegisterEnabled(
            boolean enabled) {
        buttonRegister.setEnabled(enabled);
        editFullName.setEnabled(enabled);
        editUsername.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        editPassword.setEnabled(enabled);
        editConfirmPassword.setEnabled(enabled);
    }

    private void openLogin() {
        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class);

        startActivity(intent);
        finish();
    }

    private void openLoginAfterRegister() {
        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class);

        intent.putExtra(
                "register_success",
                true);

        intent.putExtra(
                "registered_username",
                getText(editUsername));

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }
}
