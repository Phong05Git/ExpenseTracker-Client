package com.example.expensetracker.data.remote.dto.auth;

public class ChangePasswordRequestDto {
    private final String currentPassword;
    private final String newPassword;

    public ChangePasswordRequestDto(
            String currentPassword,
            String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}