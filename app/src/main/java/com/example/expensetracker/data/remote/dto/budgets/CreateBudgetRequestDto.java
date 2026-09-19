package com.example.expensetracker.data.remote.dto.budgets;

public class CreateBudgetRequestDto {
    private final int categoryId;
    private final double limitAmount;
    private final int month;
    private final int year;

    public CreateBudgetRequestDto(
            int categoryId,
            double limitAmount,
            int month,
            int year) {
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.month = month;
        this.year = year;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}