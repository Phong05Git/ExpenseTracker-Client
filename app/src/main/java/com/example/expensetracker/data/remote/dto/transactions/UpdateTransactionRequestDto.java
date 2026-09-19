package com.example.expensetracker.data.remote.dto.transactions;

public class UpdateTransactionRequestDto {
    private final int categoryId;
    private final double amount;
    private final int type;
    private final String note;
    private final String transactionDate;

    public UpdateTransactionRequestDto(
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.transactionDate = transactionDate;
    }

    public int getCategoryId() {
        return categoryId;
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
}