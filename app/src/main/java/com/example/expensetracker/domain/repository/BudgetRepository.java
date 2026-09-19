package com.example.expensetracker.domain.repository;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface BudgetRepository {

    Single<Resource<List<Budget>>> getBudgets();

    Single<Resource<Budget>> createBudget(
            int categoryId,
            double limitAmount,
            int month,
            int year);

    Single<Resource<Budget>> updateBudget(
            int id,
            int categoryId,
            double limitAmount,
            int month,
            int year);

    Single<Resource<Boolean>> deleteBudget(int id);
}