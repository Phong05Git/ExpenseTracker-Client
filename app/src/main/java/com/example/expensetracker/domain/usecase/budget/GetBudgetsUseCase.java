package com.example.expensetracker.domain.usecase.budget;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.domain.repository.BudgetRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetBudgetsUseCase {

    private final BudgetRepository budgetRepository;

    @Inject
    public GetBudgetsUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Single<Resource<List<Budget>>> execute() {
        return budgetRepository.getBudgets();
    }
}