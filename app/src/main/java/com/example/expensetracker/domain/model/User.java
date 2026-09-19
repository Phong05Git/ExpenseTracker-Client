package com.example.expensetracker.domain.model;

public final class User {
    private final int id;
    private final String username;
    private final String fullName;
    private final String email;

    public User(
            int id,
            String username,
            String fullName,
            String email) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

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