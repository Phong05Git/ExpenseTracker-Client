package com.example.expensetracker.domain.model;

public class Transaction {
    private final int id;
    private final int categoryId;
    private final String categoryName;
    private final String categoryIcon;
    private final String categoryColor;
    private final double amount;
    private final int type;
    private final String note;
    private final String transactionDate;
    private final String source;

    public Transaction(
            int id,
            int categoryId,
            String categoryName,
            String categoryIcon,
            String categoryColor,
            double amount,
            int type,
            String note,
            String transactionDate,
            String source) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryColor = categoryColor;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.transactionDate = transactionDate;
        this.source = source;
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

    public double getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getSource() {
        return source;
    }

    public boolean isIncome() {
        return type == 1;
    }
}