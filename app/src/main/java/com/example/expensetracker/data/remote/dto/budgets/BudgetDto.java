package com.example.expensetracker.data.remote.dto.budgets;

public class BudgetDto {
    private int id;
    private int categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private double limitAmount;
    private double spentAmount;
    private double remainingAmount;
    private int month;
    private int year;

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}