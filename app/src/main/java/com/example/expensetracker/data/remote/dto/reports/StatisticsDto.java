package com.example.expensetracker.data.remote.dto.reports;

import java.util.List;

public class StatisticsDto {
    private int period;
    private String startDate;
    private String endDate;
    private double totalIncome;
    private double totalExpense;
    private List<StatisticsPointDto> timeline;
    private List<CategoryBreakdownDto> expenseCategoryBreakdown;
    private List<CategoryBreakdownDto> incomeCategoryBreakdown;

    public int getPeriod() {
        return period;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public List<StatisticsPointDto> getTimeline() {
        return timeline;
    }

    public List<CategoryBreakdownDto> getExpenseCategoryBreakdown() {
        return expenseCategoryBreakdown;
    }

    public List<CategoryBreakdownDto> getIncomeCategoryBreakdown() {
        return incomeCategoryBreakdown;
    }
}