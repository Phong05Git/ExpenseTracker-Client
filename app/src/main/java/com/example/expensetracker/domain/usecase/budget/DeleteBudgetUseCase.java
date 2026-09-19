package com.example.expensetracker.domain.usecase.budget;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.repository.BudgetRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class DeleteBudgetUseCase {

    private final BudgetRepository budgetRepository;

    @Inject
    public DeleteBudgetUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Single<Resource<Boolean>> execute(int id) {
        return budgetRepository.deleteBudget(id);
    }
}