package com.example.expensetracker.data.remote.mapper;

import com.example.expensetracker.data.remote.dto.users.UserProfileDto;
import com.example.expensetracker.domain.model.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toDomain(
            UserProfileDto dto) {
        return new User(
                dto.getId(),
                dto.getUsername(),
                dto.getFullName(),
                dto.getEmail());
    }
}