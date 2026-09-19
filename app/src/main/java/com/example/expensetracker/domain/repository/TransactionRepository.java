package com.example.expensetracker.domain.repository;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface TransactionRepository {

    Single<Resource<List<Transaction>>> getTransactions(
            String fromDate,
            String toDate,
            Integer categoryId,
            Integer type,
            String keyword,
            int page,
            int pageSize);

    Single<Resource<List<Transaction>>> getTransactionsByDate(
            String date);

    Single<Resource<Transaction>> createTransaction(
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate,
            String source);

    Single<Resource<Transaction>> updateTransaction(
            int id,
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate);

    Single<Resource<Boolean>> deleteTransaction(
            int id);
}