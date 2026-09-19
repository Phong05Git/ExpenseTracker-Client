package com.example.expensetracker.domain.model;

import java.util.List;

public class StatisticsData {
    private final int period;
    private final String startDate;
    private final String endDate;
    private final double totalIncome;
    private final double totalExpense;
    private final List<StatisticsPoint> timeline;
    private final List<CategoryBreakdown> expenseCategoryBreakdown;
    private final List<CategoryBreakdown> incomeCategoryBreakdown;

    public StatisticsData(
            int period,
            String startDate,
            String endDate,
            double totalIncome,
            double totalExpense,
            List<StatisticsPoint> timeline,
            List<CategoryBreakdown> expenseCategoryBreakdown,
            List<CategoryBreakdown> incomeCategoryBreakdown) {
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.timeline = timeline;
        this.expenseCategoryBreakdown = expenseCategoryBreakdown;
        this.incomeCategoryBreakdown = incomeCategoryBreakdown;
    }

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

    public List<StatisticsPoint> getTimeline() {
        return timeline;
    }

    public List<CategoryBreakdown> getExpenseCategoryBreakdown() {
        return expenseCategoryBreakdown;
    }

    public List<CategoryBreakdown> getIncomeCategoryBreakdown() {
        return incomeCategoryBreakdown;
    }

    public static class StatisticsPoint {
        private final String date;
        private final double income;
        private final double expense;

        public StatisticsPoint(String date, double income, double expense) {
            this.date = date;
            this.income = income;
            this.expense = expense;
        }

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
}