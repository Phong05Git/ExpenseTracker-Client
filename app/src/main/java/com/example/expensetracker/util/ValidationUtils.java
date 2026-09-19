package com.example.expensetracker.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Supplier;

public final class ValidationUtils {
    private static final double MINIMUM_AMOUNT = 999D;
    private static final double MAXIMUM_AMOUNT = 9_999_999_999_999.99D;

    private ValidationUtils() {
    }

    public static boolean isValidAmount(double amount) {
        return amount > MINIMUM_AMOUNT && amount <= MAXIMUM_AMOUNT;
    }

    public static String amountError(double amount, String fieldName) {
        if (amount <= MINIMUM_AMOUNT) {
            return fieldName + " phải lớn hơn 999.";
        }

        if (amount > MAXIMUM_AMOUNT) {
            return fieldName + " không được vượt quá 9.999.999.999.999,99.";
        }

        return null;
    }

    public static boolean isValidCategory(int categoryId) {
        return categoryId > 0;
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String fullNameError(String value) {
        return isBlank(value) ? "Vui lòng nhập họ và tên." : null;
    }

    public static String usernameError(String value) {
        if (isBlank(value)) return "Vui lòng nhập tên đăng nhập.";
        int length = value.trim().length();
        return length < 3 || length > 50 ? "Tên đăng nhập phải có từ 3 đến 50 ký tự." : null;
    }

    public static String emailError(String value) {
        if (isBlank(value)) return "Vui lòng nhập email.";
        return android.util.Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()
                ? null : "Email không hợp lệ.";
    }

    public static String passwordError(String value) {
        if (isBlank(value)) return "Vui lòng nhập mật khẩu.";
        return value.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$")
                ? null : "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.";
    }

    public static String confirmPasswordError(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword)
                ? null : "Mật khẩu xác nhận không khớp.";
    }

    public static void bindLiveValidation(
            TextInputEditText editText,
            TextInputLayout inputLayout,
            Supplier<String> validator) {
        final boolean[] interacted = {false};

        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && interacted[0]) inputLayout.setError(validator.get());
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence text, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence text, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText.hasFocus() || interacted[0]) {
                    interacted[0] = true;
                    inputLayout.setError(validator.get());
                }
            }
        });
    }

    public static boolean setError(TextInputLayout inputLayout, String error) {
        inputLayout.setError(error);
        return error == null;
    }
}
