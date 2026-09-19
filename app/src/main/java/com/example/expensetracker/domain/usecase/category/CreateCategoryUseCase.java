package com.example.expensetracker.domain.usecase.category;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.domain.repository.CategoryRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public CreateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Single<Resource<Category>> execute(String name, int type, String icon, String color) {
        return categoryRepository.createCategory(name, type, icon, color);
    }
}