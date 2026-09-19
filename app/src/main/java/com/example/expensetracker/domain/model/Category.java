package com.example.expensetracker.domain.model;

public class Category {

    private final int id;
    private final Integer userId;
    private final String name;
    private final int type;
    private final String icon;
    private final String color;

    public Category(
            int id,
            Integer userId,
            String name,
            int type,
            String icon,
            String color) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
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

    public boolean isIncome() {
        return type == 1;
    }
}