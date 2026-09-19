package com.example.expensetracker.data.remote.dto.users;

public class UserProfileDto {
    private int id;
    private String username;
    private String fullName;
    private String email;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}