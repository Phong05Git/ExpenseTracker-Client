package com.example.expensetracker.data.remote.dto.auth;

public class RegisterRequestDto {
    private final String username;
    private final String password;
    private final String fullName;
    private final String email;

    public RegisterRequestDto(
            String username,
            String password,
            String fullName,
            String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}