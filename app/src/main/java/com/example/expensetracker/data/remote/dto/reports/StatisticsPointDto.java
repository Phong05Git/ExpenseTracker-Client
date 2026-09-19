package com.example.expensetracker.data.remote.dto.reports;

public class StatisticsPointDto {
    private String date;
    private double income;
    private double expense;

    public String getDate() {
        return date;
    }

    public double getIncome() {
        return income;
    }

    public double getExpense() {
        return expense;
    }
}