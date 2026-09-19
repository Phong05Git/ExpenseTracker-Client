package com.example.expensetracker.domain.usecase.category;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.repository.CategoryRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Single<Resource<Boolean>> execute(int id) {
        return categoryRepository.deleteCategory(id);
    }
}