package com.example.expensetracker.data.remote.dto.transactions;

public class TransactionDto {
    private int id;
    private int categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private double amount;
    private int type;
    private String note;
    private String transactionDate;
    private String source;

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
}