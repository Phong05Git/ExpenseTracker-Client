package com.example.expensetracker.data.remote.dto.reports;

public class CategoryBreakdownDto {
    private int categoryId;
    private String categoryName;
    private String categoryColor;
    private double amount;
    private double percentage;

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public double getAmount() {
        return amount;
    }

    public double getPercentage() {
        return percentage;
    }
}