package com.example.expensetracker.domain.usecase.transaction;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.domain.repository.TransactionRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class UpdateTransactionUseCase {
    private final TransactionRepository transactionRepository;

    @Inject
    public UpdateTransactionUseCase(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Single<Resource<Transaction>> execute(
            int id,
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate) {
        return transactionRepository.updateTransaction(
                id,
                categoryId,
                amount,
                type,
                note,
                transactionDate);
    }
}