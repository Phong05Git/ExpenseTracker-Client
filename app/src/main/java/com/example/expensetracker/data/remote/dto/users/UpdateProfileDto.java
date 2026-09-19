package com.example.expensetracker.data.remote.dto.users;

public class UpdateProfileDto {
    private final String fullName;
    private final String email;

    public UpdateProfileDto(
            String fullName,
            String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}