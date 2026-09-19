package com.example.expensetracker.domain.model;

public class CategoryBreakdown {
    private final int categoryId;
    private final String categoryName;
    private final String categoryColor;
    private final double amount;
    private final double percentage;

    public CategoryBreakdown(
            int categoryId,
            String categoryName,
            String categoryColor,
            double amount,
            double percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.amount = amount;
        this.percentage = percentage;
    }

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