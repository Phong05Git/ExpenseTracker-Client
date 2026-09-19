package com.example.expensetracker.domain.usecase.budget;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.domain.repository.BudgetRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;

    @Inject
    public CreateBudgetUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Single<Resource<Budget>> execute(
            int categoryId,
            double limitAmount,
            int month,
            int year) {

        return budgetRepository.createBudget(
                categoryId,
                limitAmount,
                month,
                year);
    }
}