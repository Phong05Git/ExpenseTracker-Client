package com.example.expensetracker.data.remote.dto.categories;

public class CategoryDto {

    private int id;
    private Integer userId;
    private String name;
    private int type;
    private String icon;
    private String color;

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
}