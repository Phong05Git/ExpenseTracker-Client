package com.example.expensetracker.data.remote.dto.categories;

public class UpdateCategoryRequestDto {
    private final String name;
    private final int type;
    private final String icon;
    private final String color;

    public UpdateCategoryRequestDto(String name, int type, String icon, String color) {
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }
}