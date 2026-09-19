package com.example.expensetracker.domain.usecase.transaction;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.domain.repository.TransactionRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetTransactionsByDateUseCase {
    private final TransactionRepository transactionRepository;

    @Inject
    public GetTransactionsByDateUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Single<Resource<List<Transaction>>> execute(String date) {
        return transactionRepository.getTransactionsByDate(date);
    }
}