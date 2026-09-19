package com.example.expensetracker.data.remote.mapper;

import com.example.expensetracker.data.remote.dto.transactions.TransactionDto;
import com.example.expensetracker.domain.model.Transaction;

import javax.inject.Inject;

public class TransactionMapper {
    @Inject
    public TransactionMapper() {
    }

    public Transaction map(TransactionDto dto) {
        return new Transaction(
                dto.getId(),
                dto.getCategoryId(),
                dto.getCategoryName(),
                dto.getCategoryIcon(),
                dto.getCategoryColor(),
                dto.getAmount(),
                dto.getType(),
                dto.getNote(),
                dto.getTransactionDate(),
                dto.getSource());
    }
}