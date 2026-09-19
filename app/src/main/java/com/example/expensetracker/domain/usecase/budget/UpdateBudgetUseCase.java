package com.example.expensetracker.domain.usecase.budget;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.domain.repository.BudgetRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class UpdateBudgetUseCase {

    private final BudgetRepository budgetRepository;

    @Inject
    public UpdateBudgetUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Single<Resource<Budget>> execute(
            int id,
            int categoryId,
            double limitAmount,
            int month,
            int year) {

        return budgetRepository.updateBudget(
                id,
                categoryId,
                limitAmount,
                month,
                year);
    }
}