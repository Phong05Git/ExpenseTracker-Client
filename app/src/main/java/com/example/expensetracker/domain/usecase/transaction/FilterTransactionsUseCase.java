package com.example.expensetracker.domain.usecase.transaction;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.domain.repository.TransactionRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class FilterTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    @Inject
    public FilterTransactionsUseCase(
            TransactionRepository transactionRepository) {
        this.transactionRepository =
                transactionRepository;
    }

    public Single<Resource<List<Transaction>>> execute(
            String fromDate,
            String toDate,
            Integer categoryId,
            Integer type,
            String keyword,
            int page,
            int pageSize) {

        return transactionRepository.getTransactions(
                fromDate,
                toDate,
                categoryId,
                type,
                keyword,
                page,
                pageSize);
    }
}