package com.example.expensetracker.domain.model;

public class Budget {
    private final int id;
    private final int categoryId;
    private final String categoryName;
    private final String categoryIcon;
    private final String categoryColor;
    private final double limitAmount;
    private final double spentAmount;
    private final double remainingAmount;
    private final int month;
    private final int year;

    public Budget(
            int id,
            int categoryId,
            String categoryName,
            String categoryIcon,
            String categoryColor,
            double limitAmount,
            double spentAmount,
            double remainingAmount,
            int month,
            int year) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryColor = categoryColor;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.month = month;
        this.year = year;
    }

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

    public boolean isOverBudget() {
        return spentAmount > limitAmount;
    }
}