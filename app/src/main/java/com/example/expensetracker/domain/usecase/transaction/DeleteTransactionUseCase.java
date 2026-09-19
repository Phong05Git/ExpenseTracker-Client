package com.example.expensetracker.domain.usecase.transaction;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.repository.TransactionRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;

    @Inject
    public DeleteTransactionUseCase(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Single<Resource<Boolean>> execute(int id) {
        return transactionRepository.deleteTransaction(id);
    }
}